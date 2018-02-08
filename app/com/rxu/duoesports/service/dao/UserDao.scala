package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.dto.{UpdateAccountInfo, UpdatePlayerInfo}
import com.rxu.duoesports.models.User
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

  def activate(id: Long): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE User SET activated = true where id = {id}
         """
      ).on('id -> id).executeUpdate()
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
            roles = {roles},
            description = {description},
            discordId = {discordId}
           WHERE id = {id}
         """
      ).on(
        'roles -> updatePlayerInfo.getRoles.mkString(","),
        'description -> updatePlayerInfo.description.orNull,
        'discordId -> updatePlayerInfo.discordId.orNull,
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
             user_role,
             summonerName,
             summoner_id,
             region,
             team_id,
             activated,
             verified,
             roles,
             description,
             discordId,
             profileImageUrl,
             timezone,
             rank,
             alts)
           VALUES(
             {email},
             {password},
             {firstName},
             {lastName},
             {user_role},
             {summonerName},
             {summoner_id},
             {region},
             {team_id},
             {activated},
             {verified},
             {roles},
             {description},
             {discordId},
             {profileImageUrl},
             {timezone},
             {rank},
             {alts})
        """
      ).on(
        'email -> user.email,
        'password -> user.password,
        'firstName -> user.firstName,
        'lastName -> user.lastName,
        'user_role -> user.user_role.toString,
        'summonerName -> user.summonerName.orNull,
        'summoner_id -> user.summoner_id.map(_.toString).orNull,
        'region -> user.region.map(_.toString).orNull,
        'team_id -> user.team_id.map(_.toString).orNull,
        'activated -> user.activated,
        'verified -> user.verified,
        'roles -> user.roles.mkString(","),
        'description -> user.description.orNull,
        'discordId -> user.discordId.orNull,
        'profileImageUrl -> user.profileImageUrl.orNull,
        'timezone -> user.timezone.toString,
        'rank -> user.rank.map(_.toString).orNull,
        'alts -> user.alts.mkString(",")
      ).executeInsert()
    }
  }
}
