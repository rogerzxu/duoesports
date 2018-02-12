package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.rxu.duoesports.models.AuthToken
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthTokenDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def findById(id: String): Future[Option[AuthToken]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM AuthToken
          WHERE id = {id}
        """
      ).on('id -> id).as(AuthToken.parser.singleOpt)
    }
  }

  def deleteByUser(userId: Long): Future[Boolean] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           DELETE FROM AuthToken
           WHERE userId = {userId}
         """
      ).on('userId -> userId).execute()
    }
  }

  def upsert(authToken: AuthToken): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO AuthToken (id, userId, expiry)
           VALUES ({id}, {userId}, {expiry})
           ON DUPLICATE KEY UPDATE
             id = {id},
             userId = {userId},
             expiry = {expiry}
         """
      ).on(
        'id -> authToken.id,
        'userId -> authToken.userId,
        'expiry -> authToken.expiry.toString
      ).execute()
    }
  }

}
