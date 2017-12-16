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

  def find(email: String): Future[Option[User]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM User
          WHERE email = {email}
        """
      ).on('email -> email).as(User.parser.singleOpt)
    }
  }

  def save(user: User): Future[Unit] = Future {
    db.withTransaction { implicit c =>
      val result = SQL(
        s"""
           INSERT INTO User (email, password, firstName, lastName, user_role, summonerName, region, team_id, activated, eligible)
           VALUES({email}, {password}, {firstName}, {lastName}, {user_role}, {summonerName}, {region}, {team_id}, {activated}, {eligible})
           ON DUPLICATE KEY UPDATE
             email = {email},
             password = {password},
             firstName = {firstName},
             lastName = {lastName},
             user_role = {user_role},
             summonerName = {summonerName},
             region = {region},
             team_id = {team_id},
             activated = {activated},
             eligible = {eligible}
        """
      ).on(
        'email -> user.email,
        'password -> user.password,
        'firstName -> user.firstName,
        'lastName -> user.lastName,
        'user_role -> user.user_role.toString,
        'summonerName -> user.summonerName.orNull,
        'region -> user.region.map(_.toString).orNull,
        'team_id -> user.team_id.map(_.toString).orNull,
        'activated -> user.activated,
        'eligible -> user.eligible
      ).execute()
    }
  }
}
