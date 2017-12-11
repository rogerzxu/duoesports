package com.rxu.duoesports.models

import anorm.{Column, Macro, MetaDataItem, RowParser, TypeDoesNotMatch}
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
  val Staff = Value("Staff")
  val Player = Value("Player")
}

object User {
  implicit val roleColumn: Column[Roles.Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "Admin" => Right(Roles.Admin)
      case "Staff" => Right(Roles.Staff)
      case "Player" => Right(Roles.Player)
      case _ => Left(TypeDoesNotMatch(s"Unknown Role $value"))
    }
  }
  implicit val regionColumn: Column[Region.Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "NA" => Right(Region.NA)
      case _ => Left(TypeDoesNotMatch(s"Unknown Region $value"))
    }
  }
  implicit val regionOptColumn = Column.columnToOption[Region.Value]
  val parser: RowParser[User] = Macro.namedParser[User]
}
