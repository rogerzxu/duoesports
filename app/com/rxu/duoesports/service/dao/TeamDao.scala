package com.rxu.duoesports.service.dao

import anorm._
import anorm.SqlParser._
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.dto.EditTeam
import com.rxu.duoesports.models.Role.Role
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

  def getCount(
    search: Option[String] = None,
    onlyRecruiting: Boolean = false,
    rolesFilter: Seq[Role] = Seq.empty
  ): Future[Int] = Future {
    db.withConnection { implicit c =>
      val searchQuery = buildSearchQuery(search, onlyRecruiting, rolesFilter)
      SQL(
        s"""
           SELECT count(id) FROM Team $searchQuery
         """
      ).as(int("count(id)").single)
    }
  }

  //TODO: Filters: division / season
  //TODO: Change if team list becomes too large
  def searchByNamesPaginated(
    offset: Int,
    limit: Int,
    search: Option[String] = None,
    onlyRecruiting: Boolean = false,
    rolesFilter: Seq[Role] = Seq.empty
  ): Future[Seq[Team]] = Future {
    db.withConnection { implicit c =>
      val queryBuilder = StringBuilder.newBuilder
      queryBuilder.append("SELECT * FROM Team")
      queryBuilder.append(buildSearchQuery(search, onlyRecruiting, rolesFilter))
      queryBuilder.append(" ORDER BY name asc LIMIT {limit} OFFSET {offset}")
      val query = queryBuilder.toString
      logger.debug(query)
      SQL(
        query
      ).on(
        'offset -> offset,
        'limit -> limit
      ).as(Team.parser.*)
    }
  }

  private def buildSearchQuery(
    search: Option[String] = None,
    onlyRecruiting: Boolean = false,
    rolesFilter: Seq[Role] = Seq.empty
  ): String = {
    val searchQuery = StringBuilder.newBuilder
    search foreach { search =>
      searchQuery.append(s" AND name like '%$search%'")
    }
    if (rolesFilter.nonEmpty) {
      searchQuery.append(s" AND (")
      searchQuery.append(rolesFilter map { role =>
        s"FIND_IN_SET('${role.toString}', recruitingRoles) > 0"
      } mkString " OR ")
      searchQuery.append(")")
    }
    if (onlyRecruiting) {
      searchQuery.append(s" AND isRecruiting = 1")
    }
    searchQuery.toString.replaceFirst("AND", "WHERE")
  }

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

  def update(teamId: Long, editTeam: EditTeam): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE Team SET
            logoUrl = {logoUrl},
            description = {description},
            isRecruiting = {isRecruiting},
            recruitingRoles = {recruitingRoles},
            discordServer = {discordServer}
           WHERE id = {id}
         """
      ).on(
        'logoUrl -> editTeam.logoUrl.orNull,
        'description -> editTeam.description.orNull,
        'isRecruiting -> editTeam.isRecruiting,
        'recruitingRoles -> editTeam.getRecruitingRoles.mkString(","),
        'discordServer -> editTeam.discordServer.orNull,
        'id -> teamId
      ).executeUpdate()
    }
  }

  def delete(teamId: Long): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           DELETE FROM Team
           WHERE id = {id}
         """
      ).on(
        'id -> teamId
      ).executeUpdate()
    }
  }

  def create(team: Team): Future[Option[Long]] = Future {
    db.withConnection { implicit c =>
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
