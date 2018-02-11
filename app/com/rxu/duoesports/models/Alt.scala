package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
import com.rxu.duoesports.models.Region.Region

case class Alt(
  summonerName: String,
  summonerId: Long,
  region: Region
) {
  override def toString: String = {
    s"$summonerName:$summonerId:${region.toString}"
  }
}

object Alt {
  implicit val altsColumn: Column[Seq[Alt]] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case alts: String => Right {
        alts.split(",").filter(_.nonEmpty) map { alt =>
          val fields = alt.split(":")
          Alt(fields(0), fields(1).toLong, Region.withName(fields(2)))
        }
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown Alt $value"))
    }
  }
}
