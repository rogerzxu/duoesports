package com.rxu.duoesports.models

import anorm.{Macro, RowParser}
import com.rxu.duoesports.models.Region.Region

import java.time.LocalDateTime

case class UserAlt(
  userId: Long,
  summonerName: String,
  summonerId: Long,
  region: Region,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
)

object UserAlt {
  val parser: RowParser[UserAlt] = Macro.namedParser[UserAlt]
}
