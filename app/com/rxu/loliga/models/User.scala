package com.rxu.loliga.models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import java.util.UUID

case class User(
  userId: UUID,
  loginInfo: LoginInfo,
  firstName: String,
  lastName: String,
  email: String,
  userType: UserTypes.Value
) extends Identity

object UserTypes extends Enumeration {
  val Admin = Value("Admin")
  val Captain = Value("Captain")
  val Player = Value("Admin")
}