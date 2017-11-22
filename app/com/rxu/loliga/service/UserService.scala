package com.rxu.loliga.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.loliga.dao.UserDao
import com.rxu.loliga.models.User

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User] {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = userDao.save(user)

}
