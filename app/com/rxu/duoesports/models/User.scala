package com.rxu.duoesports.models

import anorm.{Column, Macro, RowParser}
import com.mohiva.play.silhouette.api.Identity
import com.rxu.duoesports.models.Rank.Rank
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.Role.Role
import com.rxu.duoesports.models.Timezone.Timezone
import com.rxu.duoesports.models.UserRole.UserRole

import java.time.LocalDateTime

case class User(
  id: Long,
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  userRole: UserRole,
  summonerName: Option[String] = None,
  summonerId: Option[Long] = None,
  region: Option[Region] = None,
  teamId: Option[Long] = None,
  activated: Boolean = false,
  verified: Boolean = false,
  roles: Seq[Role] = Seq.empty,
  description: Option[String] = None,
  discordId: Option[String] = None,
  profileImageUrl: Option[String] = None,
  timezone: Timezone = Timezone.EASTERN,
  rank: Option[Rank] = None,
  isFreeAgent: Boolean = false,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
) extends Identity {

  def normalize: User = this.copy(email = email.trim.toLowerCase,
    firstName = firstName.trim.capitalize,
    lastName = lastName.trim.capitalize
  )

  def getCacheKey: String = email

  def isTeamless: Boolean = teamId.isEmpty
}

object User {
  implicit val regionOptColumn = Column.columnToOption[Region]
  val parser: RowParser[User] = Macro.namedParser[User]
}
