package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.dto.{UpdateAccountInfo, UpdatePlayerInfo, UpdatePrimarySummoner}
import com.rxu.duoesports.models.Rank.Rank
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.Role.Role
import com.rxu.duoesports.models.{User, UserAlt}
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.util.{ActivateUserException, CacheHelpers, CreateUserException, GetUserException, UpdateUserException}
import com.typesafe.scalalogging.LazyLogging
import play.api.cache.{AsyncCacheApi, NamedCache}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class UserService @Inject()(
  userDao: UserDao,
  userAltService: UserAltService,
  @NamedCache("user-email-cache") emailCache: AsyncCacheApi,
  @NamedCache("user-summonerName-cache") summonerNameCache: AsyncCacheApi
)(
  implicit val ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging
  with CacheHelpers {

  private def removeFromCache(user: User): Future[Unit] = {
    for {
      _ <- emailCache.remove(user.email)
      _ <- user.summonerName map { summonerName =>
        summonerNameCache.remove(summonerName)
      } getOrElse Future.successful(())
    } yield logger.debug(s"Invalidating ${user.email}, ${user.summonerName.getOrElse("")} from cache")
  }

  private def removeFromCache(users: Seq[User]): Future[Unit] = {
    Future.sequence {
      users map { user =>
        removeFromCache(user)
      }
    } map (_ => ())
  }

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.trace(s"Retrieving user: $loginInfo")
    val cacheKey = s"${loginInfo.providerKey}"
    cacheGetOrPut(emailCache, cacheKey, userDao.findByEmail(loginInfo.providerKey))
  }

  def create(user: User): Future[Long] = {
    logger.info(s"Creating user: $user")
    for {
      maybeUserId <- userDao.create(user)
      userId <- maybeUserId match {
        case Some(userId) => Future.successful(userId)
        case None => Future.failed(CreateUserException(s"Failed to create user $user"))
      }
      _ <- removeFromCache(user)
    } yield {
      userId
    }
  }

  def getById(id: Long): Future[User] = {
    findById(id) flatMap {
      case Some(user) => Future.successful(user)
      case None =>
        logger.error(s"Failed to get user $id")
        Future.failed(GetUserException(s"Could not find user $id"))
    }
  }

  def getByEmail(email: String): Future[User] = {
    findByEmail(email) flatMap {
      case Some(user) => Future.successful(user)
      case None =>
        logger.error(s"Failed to get user $email")
        Future.failed(GetUserException(s"Could not find user $email"))
    }
  }

  def getBySummonerName(name: String, region: Region): Future[User] = {
    findBySummonerName(name, region) flatMap {
      case Some(user) => Future.successful(user)
      case None =>
        logger.error(s"Failed to get user $name")
        Future.failed(GetUserException(s"Could not find user $name"))
    }
  }

  private def findById(id: Long): Future[Option[User]] = {
    logger.debug(s"Finding user by id $id")
    userDao.findById(id)
  }

  private def findByEmail(email: String): Future[Option[User]] = {
    logger.debug(s"Finding user by email $email")
    cacheGetOrPut(emailCache, email, userDao.findByEmail(email))
  }

  def getByTeamId(teamId: Long): Future[Seq[User]] = {
    logger.debug(s"Finding users by teamId $teamId")
    userDao.getByTeamId(teamId)
  }

  def findBySummonerName(summonerName: String, region: Region): Future[Option[User]] = {
    logger.debug(s"Finding user by summoner $summonerName $region")
    cacheGetOrPut(summonerNameCache, summonerName, userDao.findBySummonerName(summonerName, region))
  }

  def findBySummonerNameOrId(summonerName: String, summonerId: Long, region: Region): Future[Seq[User]] = {
    logger.debug(s"Finding user by summoner $summonerName or id $summonerId $region")
    userDao.findBySummonerNameOrId(summonerName, summonerId, region)
  }

  def activate(id: Long): Future[Unit] = {
    logger.info(s"Activating user by id $id")
    for {
      result <- userDao.activate(id)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to activate user $id")
          Future.failed(ActivateUserException(s"MariaDB failed to activate user $id"))
        case _ => Future.successful(())
      }
      user <- getById(id)
      _ <- removeFromCache(user)
    } yield ()
  }

  def putSummoner(user: User, summonerName: String, summonerId: Long, region: Region): Future[Unit] = {
    logger.info(s"Putting summoner for user ${user.id}: ($summonerName, $summonerId, $region)")
    for {
      result <- userDao.addSummoner(user.id, summonerName, summonerId, region)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to add summoner for user ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to add summoner for user ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(user)
    } yield ()
  }

  def updateRank(user: User, rank: Rank): Future[Unit] = {
    logger.info(s"Updating Rank for ${user.id} to $rank")
    for {
      result <- userDao.update(user.id, rank)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update rank for user ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update rank for user ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(user)
    } yield ()
  }

  def updateAccountInfo(user: User, updateAccountInfo: UpdateAccountInfo): Future[Unit] = {
    logger.info(s"Updating account info for ${user.id}: $updateAccountInfo")
    for {
      result <- userDao.update(user.id, updateAccountInfo)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update Account Info for ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update Account Info for ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(user)
    } yield ()
  }

  def updatePlayerInfo(user: User, updatePlayerInfo: UpdatePlayerInfo): Future[Unit] = {
    logger.info(s"Updating player info for ${user.id}: $updatePlayerInfo")
    for {
      result <- userDao.update(user.id, updatePlayerInfo)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update Player Info for ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update Player Info for ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(user)
    } yield ()
  }

  def updateTeamRole(summonerName: String, region: Region, teamRole: Role): Future[Unit] = {
    logger.info(s"Updating team role for ${summonerName}: $teamRole")
    for {
      result <- userDao.updateTeamRole(summonerName, region, teamRole)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update TeamRole for ${summonerName}")
          Future.failed(UpdateUserException(s"MariaDB failed to update TeamRole for ${summonerName}"))
        case _ => Future.successful(())
      }
      user <- getBySummonerName(summonerName, region)
      _ <- removeFromCache(user)
    } yield ()
  }

  def setNewPrimary(user: User, updatePrimarySummoner: UpdatePrimarySummoner): Future[Unit] = {
    logger.info(s"Updating primary summoner for ${user.id}: $updatePrimarySummoner")
    userAltService.findByUserId(user.id) flatMap { alts =>
      (for {
        newPrimaryName <- updatePrimarySummoner.getNewPrimarySummonerName
        newPrimaryInfo <- alts.find(alt => alt.summonerName.equals(newPrimaryName))
        newAlt <- (user.summonerName, user.summonerId, user.region) match {
          case (Some(summonerName), Some(summonerId), Some(region)) => Some(UserAlt(user.id, summonerName, summonerId, region))
          case _ => None
        }
      } yield {
        for {
          result <- userDao.changePrimarySummoner(user.id, newPrimaryInfo.summonerName, newPrimaryInfo.summonerId, newPrimaryInfo.region)
          _ <- result match {
            case 0 => Future.failed(UpdateUserException(s"MariaDB failed to update Summoner Info for ${user.id}"))
            case _ => Future.successful(())
          }
          _ <- userAltService.create(newAlt)
          _ <- userAltService.deleteBySummonerName(newPrimaryInfo.summonerName, newPrimaryInfo.region)
          _ <- removeFromCache(user)
        } yield ()
      }).getOrElse(Future.successful(()))
    }
  }

  def joinTeam(user: User, teamId: Long, asCaptain: Boolean = false): Future[Unit] = {
    logger.info(s"Joining team $teamId for ${user.id}")
    for {
      result <- userDao.joinTeam(user.id, teamId, asCaptain)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update Team and UserRole for ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update Team and UserRole for ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(user)
    } yield ()
  }

  def disbandTeam(teamId: Long): Future[Unit] = {
    logger.info(s"Removing users from team $teamId")
    for {
      users <- getByTeamId(teamId)
      result <- userDao.disbandTeam(teamId)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to remove users from team $teamId")
          Future.failed(UpdateUserException(s"MariaDB failed to remove users from team $teamId"))
        case _ => Future.successful(())
      }
      _ <- removeFromCache(users)
    } yield ()
  }

}
