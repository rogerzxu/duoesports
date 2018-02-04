package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

object UserRole extends Enumeration {
  type UserRole = Value

  val Admin = Value("Admin")
  val Staff = Value("Staff")
  val Player = Value("Player")

  implicit val userRoleColumn: Column[UserRole] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "Admin" => Right(UserRole.Admin)
      case "Staff" => Right(UserRole.Staff)
      case "Player" => Right(UserRole.Player)
      case _ => Left(TypeDoesNotMatch(s"Unknown UserRole $value"))
    }
  }
}
