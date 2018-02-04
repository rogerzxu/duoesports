package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

object Timezone extends Enumeration {
  type Timezone = Value

  val EASTERN = Value("US/Eastern")
  val CENTRAL = Value("US/Central")
  val MOUNTAIN = Value("US/Mountain")
  val PACIFIC = Value("US/Pacific")
  val ALASKA = Value("US/Alaska")
  val HAWAII = Value("US/Hawaii")

  implicit val timezoneColumn: Column[Timezone] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "US/Eastern" => Right(Timezone.EASTERN)
      case "US/Central" => Right(Timezone.CENTRAL)
      case "US/Mountain" => Right(Timezone.MOUNTAIN)
      case "US/Pacific" => Right(Timezone.PACIFIC)
      case "US/Alaska" => Right(Timezone.ALASKA)
      case "US/Hawaii" => Right(Timezone.HAWAII)
      case _ => Left(TypeDoesNotMatch(s"Unknown Timezone $value"))
    }
  }
}
