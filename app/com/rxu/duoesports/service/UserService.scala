package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.models.User
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.debug(s"Retrieving user: $loginInfo")
    userDao.find(email = loginInfo.providerKey)
  }

  def save(user: User): Future[Unit] = {
    logger.debug(s"Saving user: $user")
    userDao.save(user)
  }

}
