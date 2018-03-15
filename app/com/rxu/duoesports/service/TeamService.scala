package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.dto.{CreateTeamForm, EditTeam}
import com.rxu.duoesports.models.Role.Role
import com.rxu.duoesports.models.{Team, User, UserRole}
import com.rxu.duoesports.service.dao.TeamDao
import com.rxu.duoesports.util.{CacheHelpers, CreateTeamException, DuplicateTeamException, GetTeamException, UpdateTeamException}
import com.typesafe.scalalogging.LazyLogging
import play.api.cache.{AsyncCacheApi, NamedCache}

import scala.concurrent.{ExecutionContext, Future}

class TeamService @Inject()(
  teamDao: TeamDao,
  userService: UserService
)(
  implicit val ec: ExecutionContext,
  @NamedCache("team-cache") cache: AsyncCacheApi
) extends LazyLogging
  with CacheHelpers {

  def searchByNamesPaginated(
    pageNumber: Int,
    limit: Int = 10,
    search: Option[String] = None,
    onlyRecruiting: Boolean = false,
    rolesFilter: Seq[Role] = Seq.empty
  ): Future[Seq[Team]] = {
    teamDao.searchByNamesPaginated(
      (pageNumber - 1) * limit,
      limit,
      search = search,
      onlyRecruiting = onlyRecruiting,
      rolesFilter = rolesFilter
    )
  }

  def getCount(
    search: Option[String] = None,
    onlyRecruiting: Boolean = false,
    rolesFilter: Seq[Role] = Seq.empty
  ): Future[Int] = {
    teamDao.getCount(search, onlyRecruiting, rolesFilter)
  }

  def create(user: User, createTeamForm: CreateTeamForm): Future[Long] = {
    create(user, Team(
      id = 0L,
      name = createTeamForm.teamName,
      region = createTeamForm.getRegion
    ))
  }

  def create(user: User, team: Team): Future[Long] = {
    logger.info(s"Creating team: $team")
    for {
      maybeExistingTeam <- teamDao.findByName(team.name)
      _ <- maybeExistingTeam match {
        case Some(_) => Future.failed(DuplicateTeamException(s"A team already exists with the name: ${team.name}"))
        case None => Future.successful(())
      }
      maybeTeamId <- teamDao.create(team)
      teamId <- maybeTeamId match {
        case Some(teamId) => Future.successful(teamId)
        case None => Future.failed(CreateTeamException(s"Failed to create team $team"))
      }
      _ <- userService.joinTeam(user, teamId, UserRole.Captain)
      _ <- cache.remove(team.name)
      _ <- cache.remove(team.id.toString)
    } yield {
      logger.debug(s"Invalidating ${team.name} from cache")
      teamId
    }
  }

  def getById(id: Long): Future[Team] = {
    findById(id) flatMap {
      case Some(team) => Future.successful(team)
      case None =>
        logger.error(s"Failed to get team $id")
        Future.failed(GetTeamException(s"Could not find team $id"))
    }
  }

  def getByName(name: String): Future[Team] = {
    findByName(name) flatMap {
      case Some(team) => Future.successful(team)
      case None =>
        logger.error(s"Failed to get team $name")
        Future.failed(GetTeamException(s"Could not find team $name"))
    }
  }

  def findById(id: Long): Future[Option[Team]] = {
    logger.debug(s"Finding team by id $id")
    cacheGetOrPut(id.toString, teamDao.findById(id))
  }

  def findByName(name: String): Future[Option[Team]] = {
    logger.debug(s"Finding team by name $name")
    cacheGetOrPut(name, teamDao.findByName(name))
  }

  def update(team: Team, editTeam: EditTeam): Future[Unit] = {
    logger.info(s"Editing team ${team.id}: $editTeam")
    for {
      result <- teamDao.update(team.id, editTeam)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to edit team for ${team.id}")
          Future.failed(UpdateTeamException(s"MariaDB failed to edit team for ${team.id}"))
        case _ => Future.successful(())
      }
      _ <- cache.remove(team.name)
      _ <- cache.remove(team.id.toString)
    } yield logger.debug(s"Invalidating ${team.name} from cache")
  }

}
