package com.rxu.duoesports.models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import java.util.UUID

case class User(
  userId: UUID,
  loginInfo: LoginInfo,
  firstName: String,
  lastName: String,
  email: String,
  role: Roles.Value,
  summonerName: Option[String] = None,
  region: Option[Region.Value] = None,
  activated: Boolean = false,
  eligible: Boolean = false
) extends Identity

object Roles extends Enumeration {
  val Admin = Value("Admin")
  val Organizer = Value("Organizer")
  val Player = Value("Player")
}