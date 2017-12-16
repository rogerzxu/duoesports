package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

object Region extends Enumeration {
  val NA = Value("NA")

  implicit val regionColumn: Column[Region.Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "NA" => Right(Region.NA)
      case _ => Left(TypeDoesNotMatch(s"Unknown Region $value"))
    }
  }
}
