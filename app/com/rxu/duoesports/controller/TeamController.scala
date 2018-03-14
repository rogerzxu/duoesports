package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.security.{BelongsToThisTeam, DefaultEnv}
import com.rxu.duoesports.service.{TeamService, UserService}
import com.rxu.duoesports.util.ApiResponseHelpers
import com.rxu.duoesports.views.html
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class TeamController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  teamService: TeamService,
  userService: UserService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport
  with ApiResponseHelpers {

  def teamById(id: Long) = silhouette.SecuredAction(BelongsToThisTeam(id)).async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    teamService.getById(id) map { team =>
      Redirect(routes.TeamController.team(team.name).absoluteURL)
    }
  }

  def team(name: String) = silhouette.UserAwareAction.async { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    teamService.findByName(name) flatMap {
      case Some(team) => userService.getByTeamId(team.id) map { roster =>
        Ok(html.team.team(request.identity, team, roster))
      }
      case None => Future.successful(NotFound(html.errors.notFound(request.identity)))
    }
  }

  def editPage(name: String) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    teamService.findByName(name) map {
      case Some(team) => {
        val user = request.identity
        if (user.canEditTeam(team.id)) {
          Ok("")
        } else {
          Redirect(routes.HomeController.view().absoluteURL)
        }
      }
      case None => NotFound(html.errors.notFound(Some(request.identity)))
    }
  }

}

