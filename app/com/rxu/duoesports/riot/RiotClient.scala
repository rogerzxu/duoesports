package com.rxu.duoesports.riot

import com.google.inject.Inject
import com.rxu.duoesports.config.AppConfig
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.riot.dto.{RiotSummoner, RiotSummonerLeague}
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

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

  private def executeRequest[T](path: String, region: Region)(parser: WSResponse => T): Future[T] = {
    logger.info(s"GET $path")
    val url = buildUrl(path, region)
    ws.url(url)
      .withHttpHeaders("X-RIOT-TOKEN" -> appConfig.riotApiKey)
      .withRequestTimeout(10.seconds)
      .get map { response =>
      logger.info(s"Received ${response.status} response from $url")
      logger.info(s"Response body: ${response.body}")
      parser(response)
    }
  }

  def findBySummonerName(summonerName: String, region: Region): Future[Option[RiotSummoner]] = {
    val path = s"/lol/summoner/v3/summoners/by-name/$summonerName"
    executeRequest(path, region){ response =>
      Json.parse(response.body).asOpt[RiotSummoner]
    }
  }

  def getVerificationCode(summonerId: Long, region: Region): Future[String] = {
    val path = s"/lol/platform/v3/third-party-code/by-summoner/$summonerId"
    Thread.sleep(3000) //TODO: really don't like to do this, but RIOT API is slow to update
    executeRequest(path, region)(_.body.replaceAll("\"", ""))
  }

  def getLeagueForSummoner(summonerId: Long, region: Region): Future[Seq[RiotSummonerLeague]] = {
    val path = s"/lol/league/v3/positions/by-summoner/$summonerId"
    executeRequest(path, region){ response =>
      Json.parse(response.body).as[Seq[RiotSummonerLeague]]
    }
  }

}
