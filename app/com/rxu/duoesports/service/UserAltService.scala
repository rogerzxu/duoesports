package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.UserAlt
import com.rxu.duoesports.service.dao.UserAltQueries
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class UserAltService @Inject()(
  userAltDao: UserAltQueries
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def findByUserId(userId: Long): Future[Seq[UserAlt]] = {
    logger.debug(s"Finding UserAlt's by userId $userId")
    userAltDao.findByUserId(userId)
  }

  def findBySummonerName(summonerName: String, region: Region): Future[Option[UserAlt]] = {
    logger.debug(s"Finding User Alt by summoner $summonerName $region")
    userAltDao.findBySummonerName(summonerName, region)
  }

  def findBySummonerNameOrId(summonerName: String, summonerId: Long, region: Region): Future[Seq[UserAlt]] = {
    logger.debug(s"Finding User Alt by summoner $summonerName or id $summonerId $region")
    userAltDao.findBySummonerNameOrId(summonerName, summonerId, region)
  }

  def create(userAlt: UserAlt): Future[Unit] = {
    logger.debug(s"Creating UserAlt $userAlt")
    userAltDao.insert(userAlt)
  }

}
