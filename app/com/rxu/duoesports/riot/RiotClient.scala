package com.rxu.duoesports.riot

import com.google.inject.Inject
import com.rxu.duoesports.config.AppConfig
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.riot.dto.RiotSummoner
import com.rxu.duoesports.util.RiotApiException
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class RiotClient @Inject()(
  appConfig: AppConfig,
  ws: WSClient
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  //todo: support other regions
  private val riotRegions = scala.collection.immutable.HashMap(
    "NA" -> "na1"
  )

  private def buildUrl(path: String, region: Region): String = {
    appConfig.riotBaseUrl.replaceFirst("region", riotRegions(region.toString)) + path
  }

  def getSummonerByName(summonerName: String, region: Region): Future[RiotSummoner] = {
    val path = s"/lol/summoner/v3/summoners/by-name/$summonerName"
    val url = buildUrl(path, region)
    logger.info(s"GET $path")
    ws.url(url).get map { response =>
      logger.info(s"Received ${response.status} response from $url")
      logger.info(s"Response body: ${response.body}")
      Json.parse(response.body).validate[RiotSummoner].fold(
        _ => throw RiotApiException(s"Unable to parse response ${response.body} from $url"),
        riotSummoner => riotSummoner
      )
    }
  }

}
