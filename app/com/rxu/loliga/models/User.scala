package com.rxu.loliga.models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import java.util.UUID

case class User(
  userId: UUID,
  loginInfo: LoginInfo,
  firstName: String,
  lastName: String,
  email: String,
  summonerName: String,
  region: Region.Value,
  userType: UserTypes.Value
) extends Identity

object UserTypes extends Enumeration {
  val Admin = Value("Admin")
  val Organizer = Value("Organizer")
  val Player = Value("Player")
}