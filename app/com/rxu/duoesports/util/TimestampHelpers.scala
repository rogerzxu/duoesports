package com.rxu.duoesports.util

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

import java.sql.Timestamp

trait TimestampHelpers {

  implicit val timestampColumn: Column[Timestamp] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case timestamp: Timestamp => Right(timestamp)
      case timestampStr: String => Right {
        Timestamp.valueOf(timestampStr)
      }
      case _ => Left(TypeDoesNotMatch(s"Cannot parse timestamp: $value"))
    }
  }

}
