package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.models.{User, VerificationCode}
import com.rxu.duoesports.riot.RiotClient
import com.rxu.duoesports.service.dao.VerificationCodeDao
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class VerificationService @Inject()(
  verificationCodeDao: VerificationCodeDao,
  userService: UserService,
  riotClient: RiotClient
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def generateVerificationCode(user: User): Future[VerificationCode] = {
    val verificationCode = VerificationCode(
      userId = user.id,
      code = UUID.randomUUID.toString
    )
    verificationCodeDao.upsert(verificationCode) map {_ => verificationCode}
  }

}
