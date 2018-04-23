package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

import scala.util.{Success, Try}

object NotificationType extends Enumeration {
  type NotificationType = Value
  
  val APPLICATION = Value("APPLICATION")
  val SYSTEM = Value("SYSTEM")
  val INVITE = Value("INVITE")
  
  implicit val notificationTypeColumn: Column[NotificationType] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case s: String => Try(NotificationType.withName(s)) match {
        case Success(notificationType) => Right(notificationType)
        case _ => Left(TypeDoesNotMatch(s"Unknown NotificationType $value"))
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown NotificationType $value"))
    }
  }

}
