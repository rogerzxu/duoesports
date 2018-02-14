package com.rxu.duoesports.riot.dto

import com.rxu.duoesports.models.Rank
import com.rxu.duoesports.models.Rank.Rank
import play.api.libs.json.{Format, Json}

case class RiotSummonerLeague(
  rank: String,
  queueType: String,
  hotStreak: Boolean,
  miniSeries: Option[MiniSeries],
  wins: Int,
  veteran: Boolean,
  losses: Int,
  freshBlood: Boolean,
  leagueId: String,
  playerOrTeamName: String,
  inactive: Boolean,
  playerOrTeamId: String,
  leagueName: String,
  tier: String,
  leaguePoints: Int
) {
  def getRank: Rank = {
    tier match {
      case "CHALLENGER" => Rank.CHALLENGER
      case "MASTER" => Rank.MASTER
      case _ => Rank.withName(s"$tier $rank")
    }
  }
  def isSoloQOrFlex: Boolean = {
    queueType == "RANKED_FLEX_SR" || queueType == "RANKED_SOLO_5x5"
  }
}

case class MiniSeries(
  wins: Int,
  losses: Int,
  target: Int,
  progress: String
)

object MiniSeries {
  implicit val miniSeriesFormat: Format[MiniSeries] = Json.format[MiniSeries]
}

object RiotSummonerLeague {
  implicit val riotSummonerLeagueFormat: Format[RiotSummonerLeague] = Json.format[RiotSummonerLeague]
}
