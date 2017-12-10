package com.rxu.duoesports.models

import com.mohiva.play.silhouette.api.Identity

case class User(
  id: Option[Long],
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  role: Roles.Value,
  summonerName: Option[String] = None,
  region: Option[Region.Value] = None,
  team_id: Option[Long] = None,
  activated: Boolean = false,
  eligible: Boolean = false
) extends Identity

object Roles extends Enumeration {
  val Admin = Value("Admin")
  val Organizer = Value("Organizer")
  val Player = Value("Player")
}