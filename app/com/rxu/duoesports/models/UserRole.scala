package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

import scala.util.{Success, Try}

object UserRole extends Enumeration {
  type UserRole = Value

  val Captain = Value("Captain")
  val Admin = Value("Admin")
  val Staff = Value("Staff")
  val Player = Value("Player")

  implicit val userRoleColumn: Column[UserRole] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case s: String => Try(UserRole.withName(s)) match {
        case Success(userRole) => Right(userRole)
        case _ => Left(TypeDoesNotMatch(s"Unknown UserRole $value"))
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown UserRole $value"))
    }
  }
}
