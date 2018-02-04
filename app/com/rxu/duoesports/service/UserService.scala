package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.dto.UpdateAccountInfo
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.models.User
import com.rxu.duoesports.util.{CreateUserException, UpdateUserException}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.trace(s"Retrieving user: $loginInfo")
    userDao.findByEmail(email = loginInfo.providerKey)
  }

  def create(user: User): Future[Long] = {
    logger.info(s"Creating user: $user")
    userDao.create(user) map {
      case Some(userId) => userId
      case None => throw CreateUserException(s"Failed to create user $user")
    }
  }

  def findById(id: Long): Future[Option[User]] = {
    logger.debug(s"Finding user by id $id")
    userDao.findById(id)
  }

  def findByEmail(email: String): Future[Option[User]] = {
    logger.debug(s"Finding user by email $email")
    userDao.findByEmail(email)
  }

  def activate(id: Long): Future[Int] = {
    logger.info(s"Activating user by id $id")
    userDao.activate(id)
  }

  def update(userId: Long, updateAccountInfo: UpdateAccountInfo): Future[Unit] = {
    logger.info(s"Updating account info for $userId: $updateAccountInfo")
    userDao.update(userId, updateAccountInfo) flatMap {
      case 0 => logger.error(s"MariaDB failed to update Account Info for $userId")
        Future.failed(UpdateUserException(s"MariaDB failed to update Account Info for $userId"))
      case _ => Future.successful(())
    }
  }

}
