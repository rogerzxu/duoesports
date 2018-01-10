package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.models.User
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging {

  def getVerificationCode(email: String): Future[String] = {
    val verificationCode = UUID.randomUUID.toString
    userDao.addVerificationCode(email, verificationCode) map (_ => verificationCode)
  }

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.debug(s"Retrieving user: $loginInfo")
    userDao.find(email = loginInfo.providerKey)
  }

  def save(user: User): Future[Unit] = {
    logger.debug(s"Saving user: $user")
    userDao.save(user)
  }

  def findById(id: Long): Future[Option[User]] = {
    logger.debug(s"Finding user by id $id")
    userDao.find(id)
  }

}
