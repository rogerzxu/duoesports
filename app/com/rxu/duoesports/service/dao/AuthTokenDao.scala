package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.rxu.duoesports.models.AuthToken
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class AuthTokenDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging{

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
           WHERE user_id = {user_id}
         """
      ).on('user_id -> userId).execute()
    }
  }

  def upsert(authToken: AuthToken): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO AuthToken (id, user_id, expiry)
           VALUES ({id}, {user_id}, {expiry})
           ON DUPLICATE KEY UPDATE
             id = {id},
             user_id = {user_id},
             expiry = {expiry}
         """
      ).on(
        'id -> authToken.id,
        'user_id -> authToken.user_id,
        'expiry -> authToken.expiry.toString
      ).execute()
    }
  }

}
