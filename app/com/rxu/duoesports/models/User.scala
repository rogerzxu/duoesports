package com.rxu.duoesports.models

import com.mohiva.play.silhouette.api.Identity

import java.util.UUID

case class User(
  userId: UUID,
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  role: Roles.Value,
  summonerName: Option[String] = None,
  region: Option[Region.Value] = None,
  teamId: Option[UUID] = None,
  activated: Boolean = false,
  eligible: Boolean = false
) extends Identity

object Roles extends Enumeration {
  val Admin = Value("Admin")
  val Organizer = Value("Organizer")
  val Player = Value("Player")
}