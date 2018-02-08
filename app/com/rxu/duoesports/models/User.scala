package com.rxu.duoesports.models

import anorm.{Column, Macro, MetaDataItem, RowParser, TypeDoesNotMatch}
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
  user_role: UserRole,
  summonerName: Option[String] = None,
  summoner_id: Option[Long] = None,
  region: Option[Region] = None,
  team_id: Option[Long] = None,
  activated: Boolean = false,
  verified: Boolean = false,
  roles: Seq[Role] = Seq.empty,
  description: Option[String] = None,
  discordId: Option[String] = None,
  profileImageUrl: Option[String] = None,
  timezone: Timezone = Timezone.EASTERN,
  rank: Option[Rank] = None,
  alts: Seq[String] = Seq.empty,
  created_at: LocalDateTime = LocalDateTime.now(),
  updated_at: LocalDateTime = LocalDateTime.now()
) extends Identity {

  def normalize: User = this.copy(email = email.trim.toLowerCase,
    firstName = firstName.trim.capitalize,
    lastName = lastName.trim.capitalize
  )

  def getCacheKey: String = s"user-$email"
}

object User {
  implicit val altsColumn: Column[Seq[String]] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case alts: String => Right {
        alts.split(",").filter(_.nonEmpty)
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown Role $value"))
    }
  }
  implicit val regionOptColumn = Column.columnToOption[Region]
  val parser: RowParser[User] = Macro.namedParser[User]
}
