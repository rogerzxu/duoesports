package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.dao.UserDao
import com.rxu.duoesports.models.User
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.debug(s"Retrieving user: $loginInfo")
    userDao.find(loginInfo)
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = {
    logger.info(s"Creating user: $user")
    userDao.save(user)
  }

}
