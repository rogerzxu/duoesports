package com.rxu.duoesports.riot.dto

import play.api.libs.json.{Format, Json}

case class RiotSummoner(
  profileIconId: Int,
  name: String,
  summonerLevel: Long,
  revisionDate: Long,
  id: Long,
  accountId: Long
)

object RiotSummoner {
  implicit val riotSummonerFormat: Format[RiotSummoner] = Json.format[RiotSummoner]
}
