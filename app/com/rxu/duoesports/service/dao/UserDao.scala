package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
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

  def activate(id: Long): Future[Unit] = Future {
    db.withConnection { implicit c =>
      val result = SQL(
        s"""
           UPDATE User SET activated = true where id = {id}
         """
      ).on('id -> id).executeUpdate()
    }
  }

  def addVerificationCode(email: String, code: String): Future[Unit] = Future {
    db.withConnection { implicit c =>
      val result = SQL(
        s"""
           UPDATE User SET verification_code = {verification_code} where email = {email}
         """
      ).on(
        'verification_code -> code,
        'email -> email
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
             verification_code,
             team_id,
             roles,
             activated,
             eligible)
           VALUES(
             {email},
             {password},
             {firstName},
             {lastName},
             {user_role},
             {summonerName},
             {summoner_id},
             {region},
             {verification_code},
             {team_id},
             {roles},
             {activated},
             {eligible})
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
        'verification_code -> user.verification_code.orNull,
        'team_id -> user.team_id.map(_.toString).orNull,
        'roles -> user.roles.mkString(","),
        'activated -> user.activated,
        'eligible -> user.eligible
      ).executeInsert()
    }
  }
}
