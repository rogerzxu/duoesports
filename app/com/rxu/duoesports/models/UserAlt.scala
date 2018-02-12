package com.rxu.duoesports.models

import anorm.{Macro, RowParser}
import com.rxu.duoesports.models.Region.Region

case class UserAlt(
  userId: Long,
  summonerName: String,
  summonerId: Long,
  region: Region
)

object UserAlt {
  val parser: RowParser[UserAlt] = Macro.namedParser[UserAlt]
}
