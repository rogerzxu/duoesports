package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.dto.{UpdateAccountInfo, UpdatePlayerInfo}
import com.rxu.duoesports.models.Rank.Rank
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.User
import com.rxu.duoesports.models.UserRole.UserRole
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val executionContext: ExecutionContext
) extends LazyLogging {

  def findById(id: Long): Future[Option[User]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           SELECT * FROM User
           WHERE id = {id}
         """
      ).on('id -> id).as(User.parser.singleOpt)
    }
  }

  def findByEmail(email: String): Future[Option[User]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM User
          WHERE email = {email}
        """
      ).on('email -> email).as(User.parser.singleOpt)
    }
  }

  def findBySummonerName(summonerName: String, region: Region): Future[Option[User]] = Future {
    db.withConnection {implicit c =>
      SQL(
        s"""
          SELECT * FROM User
          WHERE summonerName = {summonerName}
          AND region = {region}
        """
      ).on(
        'summonerName -> summonerName,
        'region -> region.toString).as(User.parser.singleOpt)
    }
  }

  def findBySummonerNameOrId(summonerName: String, summonerId: Long, region: Region): Future[Seq[User]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM User
          WHERE (summonerName = {summonerName} OR summonerId = {summonerId})
          AND region = {region}
        """
      ).on(
        'summonerName -> summonerName,
        'summonerId -> summonerId,
        'region -> region.toString).as(User.parser.*)
    }
  }

  def getByTeamId(teamId: Long): Future[Seq[User]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM User
          WHERE teamId = {teamId}
        """
      ).on(
        'teamId -> teamId).as(User.parser.*)
    }
  }

  def activate(id: Long): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET activated = true where id = {id}
         """
      ).on('id -> id).executeUpdate()
    }
  }

  def addSummoner(id: Long, summonerName: String, summonerId: Long, region: Region): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET
            verified = true,
            summonerName = {summonerName},
            summonerId = {summonerId},
            region = {region}
           WHERE id = {id}
         """
      ).on(
        'summonerName -> summonerName,
        'summonerId -> summonerId,
        'region -> region.toString,
        'id -> id
      ).executeUpdate()
    }
  }

  def update(id: Long, rank: Rank): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET
            rank = {rank}
           WHERE id = {id}
         """
      ).on(
        'rank -> rank.toString,
        'id -> id
      ).executeUpdate()
    }
  }

  def update(userId: Long, updateAccountInfo: UpdateAccountInfo): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET
            firstName = {firstName},
            lastName = {lastName},
            timezone = {timezone}
           WHERE id = {id}
         """
      ).on(
        'firstName -> updateAccountInfo.firstName,
        'lastName -> updateAccountInfo.lastName,
        'timezone -> updateAccountInfo.getTimezone.toString,
        'id -> userId
      ).executeUpdate()
    }
  }

  def update(userId: Long, updatePlayerInfo: UpdatePlayerInfo): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET
            profileImageUrl = {profileImage},
            description = {description},
            discordId = {discordId},
            isFreeAgent = {isFreeAgent},
            freeAgentRoles = {freeAgentRoles}
           WHERE id = {id}
         """
      ).on(
        'profileImage -> updatePlayerInfo.profileImage.orNull,
        'description -> updatePlayerInfo.description.orNull,
        'discordId -> updatePlayerInfo.discordId.orNull,
        'isFreeAgent -> updatePlayerInfo.isFreeAgent,
        'freeAgentRoles -> updatePlayerInfo.getFreeAgentRoles.mkString(","),
        'id -> userId
      ).executeUpdate()
    }
  }

  def changePrimarySummoner(
    userId: Long,
    newPrimaryName: String,
    newPrimaryId: Long,
    newPrimaryRegion: Region): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User Set
            summonerName = {summonerName},
            summonerId = {summonerId},
            region = {region}
           WHERE id = {id}
         """
      ).on(
        'summonerName -> newPrimaryName,
        'summonerId -> newPrimaryId,
        'region -> newPrimaryRegion.toString,
        'id -> userId
      ).executeUpdate()
    }
  }

  def joinTeam(userId: Long, teamId: Long, userRole: UserRole): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User Set
            teamId = {teamId},
            userRole = {userRole},
            isFreeAgent = 0
           WHERE id = {id}
         """
      ).on(
        'teamId -> teamId,
        'userRole -> userRole.toString,
        'id -> userId
      ).executeUpdate()
    }
  }

  def create(user: User): Future[Option[Long]] = Future {
    db.withTransaction { implicit c =>
      SQL(
        s"""
           INSERT INTO User
             (email,
             password,
             firstName,
             lastName,
             userRole,
             summonerName,
             summonerId,
             region,
             rank,
             teamId,
             teamRole,
             activated,
             verified,
             description,
             discordId,
             profileImageUrl,
             timezone,
             isFreeAgent,
             freeAgentRoles)
           VALUES(
             {email},
             {password},
             {firstName},
             {lastName},
             {userRole},
             {summonerName},
             {summonerId},
             {region},
             {rank},
             {teamId},
             {teamRole},
             {activated},
             {verified},
             {description},
             {discordId},
             {profileImageUrl},
             {timezone},
             {isFreeAgent},
             {freeAgentRoles})
        """
      ).on(
        'email -> user.email,
        'password -> user.password,
        'firstName -> user.firstName,
        'lastName -> user.lastName,
        'userRole -> user.userRole.toString,
        'summonerName -> user.summonerName.orNull,
        'summonerId -> user.summonerId.map(_.toString).orNull,
        'region -> user.region.map(_.toString).orNull,
        'rank -> user.rank.map(_.toString).orNull,
        'teamId -> user.teamId.map(_.toString).orNull,
        'teamRole -> user.teamRole.map(_.toString).orNull,
        'activated -> user.activated,
        'verified -> user.verified,
        'description -> user.description.orNull,
        'discordId -> user.discordId.orNull,
        'profileImageUrl -> user.profileImageUrl.orNull,
        'timezone -> user.timezone.toString,
        'isFreeAgent -> user.isFreeAgent,
        'freeAgentRoles -> user.freeAgentRoles.mkString(",")
      ).executeInsert()
    }
  }
}
