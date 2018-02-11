package com.rxu.duoesports.service.dao

import anorm.SQL
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.rxu.duoesports.models.VerificationCode
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
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
           VALUES ({user_id}, {code})
           ON DUPLICATE KEY UPDATE
             user_id = {user_id},
             code = {code}
         """
      ).on(
        'user_id -> verificationCode.user_id,
        'code -> verificationCode.code
      ).execute()
    }
  }

}
