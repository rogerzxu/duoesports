package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.models.Team
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val executionContext: ExecutionContext
) extends LazyLogging {

  def findById(id: Long): Future[Option[Team]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           SELECT * FROM Team
           WHERE id = {id}
         """
      ).on('id -> id).as(Team.parser.singleOpt)
    }
  }

  def findByName(name: String): Future[Option[Team]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           SELECT * FROM Team
           WHERE name = {name}
         """
      ).on('name -> name).as(Team.parser.singleOpt)
    }
  }

  def create(team: Team): Future[Option[Long]] = Future {
    db.withTransaction { implicit c =>
      SQL(
        s"""
           INSERT INTO Team
            (name,
            region,
            divisionId,
            seasonId,
            description,
            logoUrl,
            eligible,
            isRecruiting,
            recruitingRoles,
            discordServer)
           VALUES(
            {name},
            {region},
            {divisionId},
            {seasonId},
            {description},
            {logoUrl},
            {eligible},
            {isRecruiting},
            {recruitingRoles},
            {discordServer})
         """
      ).on(
        'name -> team.name,
        'region -> team.region.toString,
        'divisionId -> team.divisionId.map(_.toString).orNull,
        'seasonId -> team.seasonId.map(_.toString).orNull,
        'description -> team.description.orNull,
        'logoUrl -> team.logoUrl.orNull,
        'eligible -> team.eligible,
        'isRecruiting -> team.isRecruiting,
        'recruitingRoles -> team.recruitingRoles.mkString(","),
        'discordServer -> team.discordServer.orNull
      ).executeInsert()
    }
  }

}
