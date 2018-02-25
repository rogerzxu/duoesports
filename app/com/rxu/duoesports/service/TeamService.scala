package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.models.Team
import com.rxu.duoesports.service.dao.TeamDao
import com.rxu.duoesports.util.{CacheHelpers, CreateTeamException, GetTeamException}
import com.typesafe.scalalogging.LazyLogging
import play.api.cache.{AsyncCacheApi, NamedCache}

import scala.concurrent.{ExecutionContext, Future}

class TeamService @Inject()(
  teamDao: TeamDao
)(
  implicit val ec: ExecutionContext,
  @NamedCache("team-cache") cache: AsyncCacheApi
) extends LazyLogging
  with CacheHelpers {

  def create(team: Team): Future[Long] = {
    logger.info(s"Creating team: $team")
    for {
      maybeTeamId <- teamDao.create(team)
      teamId <- maybeTeamId match {
        case Some(teamId) => Future.successful(teamId)
        case None => Future.failed(CreateTeamException(s"Failed to create team $team"))
      }
      _ <- cache.remove(team.name)
    } yield {
      logger.debug(s"Invalidating ${team.name} from cache")
      teamId
    }
  }

  def getById(id: Long): Future[Team] = {
    logger.debug(s"Finding team by id $id")
    teamDao.findById(id) flatMap {
      case Some(team) => Future.successful(team)
      case None =>
        logger.error(s"Failed to get team $id")
        Future.failed(GetTeamException(s"Could not find team $id"))
    }
  }

  def getByName(name: String): Future[Team] = {
    logger.debug(s"Finding team by name $name")
    teamDao.findByName(name) flatMap {
      case Some(team) => Future.successful(team)
      case None =>
        logger.error(s"Failed to get team $name")
        Future.failed(GetTeamException(s"Could not find team $name"))
    }
  }

}
