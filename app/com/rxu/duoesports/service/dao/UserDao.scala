package com.rxu.duoesports.service.dao

import com.google.inject.Singleton
import com.mohiva.play.silhouette.api.LoginInfo
import com.rxu.duoesports.models.User

import scala.collection.mutable
import scala.concurrent.Future

@Singleton
class UserDao {
  val users: mutable.HashMap[Long, User] = mutable.HashMap()

  def find(loginInfo: LoginInfo) = Future.successful(
    users.find { case (_, user) => user.email == loginInfo.providerKey }.map(_._2)
  )

  def find(id: Long) = Future.successful(users.get(id))

  def save(user: User) = {
    users += (user.id.getOrElse(0L) -> user) //TODO: fix
    Future.successful(user)
  }
}
