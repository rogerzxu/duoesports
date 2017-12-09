package com.rxu.duoesports.models

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
  role: Roles.Value,
  activated: Boolean
) extends Identity

object Roles extends Enumeration {
  val Admin = Value("Admin")
  val Organizer = Value("Organizer")
  val Player = Value("Player")
}