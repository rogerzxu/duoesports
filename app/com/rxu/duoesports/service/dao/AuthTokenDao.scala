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
          WHERE email = {email}
        """
      ).on('id -> id).as(AuthToken.parser.singleOpt)
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
             expiry = {expiryId}
         """
      ).on(
        'id -> authToken.id,
        'userId -> authToken.user_id,
        'expiry -> authToken.expiry
      ).execute()
    }
  }

}
