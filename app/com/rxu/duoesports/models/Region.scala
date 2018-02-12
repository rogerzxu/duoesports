package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
import play.api.libs.json.{Format, JsResult, JsString, JsSuccess, JsValue}

object Region extends Enumeration {
  type Region = Value
  val NA = Value("NA")

  implicit val regionFormat: Format[Region] = new Format[Region] {
    override def reads(json: JsValue): JsResult[Region] = JsSuccess(Region.withName(json.as[String]))
    override def writes(region: Region): JsValue = JsString(region.toString)
  }

  implicit val regionColumn: Column[Region] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case "NA" => Right(Region.NA)
      case _ => Left(TypeDoesNotMatch(s"Unknown Region $value"))
    }
  }
}
