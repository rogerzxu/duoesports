package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.models.User
import com.rxu.duoesports.util.CreateUserException
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
    logger.debug(s"Creating RIOT verification code for $email")
    val verificationCode = UUID.randomUUID.toString
    userDao.addVerificationCode(email, verificationCode) map (_ => verificationCode)
  }

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.debug(s"Retrieving user: $loginInfo")
    userDao.findByEmail(email = loginInfo.providerKey)
  }

  def create(user: User): Future[Long] = {
    logger.debug(s"Creating user: $user")
    userDao.create(user) map {
      case Some(userId) => userId
      case None => throw CreateUserException(s"Failed to create user $user")
    }
  }

  def findById(id: Long): Future[Option[User]] = {
    logger.debug(s"Finding user by id $id")
    userDao.findById(id)
  }

  def activate(id: Long): Future[Unit] = {
    logger.debug(s"Activating user by id $id")
    userDao.activate(id)
  }

}
