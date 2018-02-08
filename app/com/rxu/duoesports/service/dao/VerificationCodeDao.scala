package com.rxu.duoesports.service.dao

import anorm.SQL
import com.google.inject.Inject
import com.google.inject.name.Named
import com.rxu.duoesports.models.VerificationCode
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class VerificationCodeDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def upsert(verificationCode: VerificationCode): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO VerificationCode (user_id, code)
           VALUES ({user_id}, {expiry})
           ON DUPLICATE KEY UPDATE
             user_id = {user_id},
             expiry = {expiry}
         """
      ).on(
        'user_id -> verificationCode.user_id,
        'expiry -> verificationCode.code
      ).execute()
    }
  }

}
