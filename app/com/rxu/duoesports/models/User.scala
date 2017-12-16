package com.rxu.duoesports.models

import anorm.{Column, Macro, MetaDataItem, RowParser, TypeDoesNotMatch}
import com.mohiva.play.silhouette.api.Identity

case class User(
  id: Option[Long],
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  user_role: UserRole.Value,
  summonerName: Option[String] = None,
  region: Option[Region.Value] = None,
  verification_code: Option[String] = None,
  team_id: Option[Long] = None,
  roles: List[Role.Value] = List.empty,
  activated: Boolean = false,
  eligible: Boolean = false
) extends Identity {

  def normalize: User = this.copy(
    email = email.trim.toLowerCase,
    firstName = firstName.trim.capitalize,
    lastName = lastName.trim.capitalize
  )
}

object UserRole extends Enumeration {
  val Admin = Value("Admin")
  val Staff = Value("Staff")
  val Player = Value("Player")

  implicit val userRoleColumn: Column[UserRole.Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "Admin" => Right(UserRole.Admin)
      case "Staff" => Right(UserRole.Staff)
      case "Player" => Right(UserRole.Player)
      case _ => Left(TypeDoesNotMatch(s"Unknown UserRole $value"))
    }
  }
}

object Role extends Enumeration {
  val Top = Value("Top")
  val Jungle = Value("Jungle")
  val Middle = Value("Middle")
  val Bottom = Value("Bottom")
  val Support = Value("Support")
  val Coach = Value("Coach")
  val Analyst = Value("Analyst")

  implicit val roleColumn: Column[Role.Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "Top" => Right(Role.Top)
      case "Jungle" => Right(Role.Jungle)
      case "Middle" => Right(Role.Middle)
      case "Bottom" => Right(Role.Bottom)
      case "Support" => Right(Role.Support)
      case "Coach" => Right(Role.Coach)
      case "Analyst" => Right(Role.Analyst)
      case _ => Left(TypeDoesNotMatch(s"Unknown Role $value"))
    }
  }
}

object User {
  implicit val roleSeqColumn = Column.columnToList[Role.Value]
  implicit val regionOptColumn = Column.columnToOption[Region.Value]
  val parser: RowParser[User] = Macro.namedParser[User]
}
