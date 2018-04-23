package com.rxu.duoesports.service.dao

import anorm.{Row, SQL, SimpleSql}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.UserAlt
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAltQueries @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def findByUserId(userId: Long): Future[Seq[UserAlt]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM UserAlt
          WHERE userId = {userId}
        """
      ).on('userId -> userId).as(UserAlt.parser.*)
    }
  }

  def findBySummonerName(summonerName: String, region: Region): Future[Option[UserAlt]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM UserAlt
          WHERE summonerName = {summonerName}
          AND region = {region}
        """
      ).on(
        'summonerName -> summonerName,
        'region -> region.toString
      ).as(UserAlt.parser.singleOpt)
    }
  }

  def findBySummonerNameOrId(summonerName: String, summonerId: Long, region: Region): Future[Seq[UserAlt]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM UserAlt
          WHERE (summonerName = {summonerName} OR summonerId = {summonerId})
          AND region = {region}
        """
      ).on(
        'summonerName -> summonerName,
        'summonerId -> summonerId,
        'region -> region.toString
      ).as(UserAlt.parser.*)
    }
  }

  def insert(userAlt: UserAlt): Future[Unit] = Future {
    db.withConnection { implicit c =>
      UserAltQueries.insert(userAlt).executeInsert()
    }
  }

}

object UserAltQueries {
  def insert(userAlt: UserAlt): SimpleSql[Row] = {
    SQL(
      s"""
           INSERT INTO UserAlt (userId, summonerName, summonerId, region)
           VALUES ({userId}, {summonerName}, {summonerId}, {region})
         """
    ).on(
      'userId -> userAlt.userId,
      'summonerName -> userAlt.summonerName,
      'summonerId -> userAlt.summonerId,
      'region -> userAlt.region.toString
    )
  }

  def deleteQuery(summonerName: String, region: Region): SimpleSql[Row] = {
    SQL(
      s"""
           DELETE FROM UserAlt
           WHERE summonerName = {summonerName}
           AND region = {region}
         """
    ).on(
      'summonerName -> summonerName,
      'region -> region.toString
    )
  }
}
