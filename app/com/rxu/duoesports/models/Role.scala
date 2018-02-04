package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

object Role extends Enumeration {
  type Role = Value
  val Top = Value("Top")
  val Jungle = Value("Jungle")
  val Middle = Value("Middle")
  val Bottom = Value("Bottom")
  val Support = Value("Support")
  val Coach = Value("Coach")
  val Analyst = Value("Analyst")

  implicit val roleSeqColumn: Column[Seq[Role]] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case roles: String => Right {
        roles.split(",").filter(_.nonEmpty).map { role =>
          Role.withName(role)
        }.toSeq
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown Role $value"))
    }
  }
}