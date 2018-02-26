package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.dto.CreateTeamForm
import com.rxu.duoesports.security.{CanCreateTeam, DefaultEnv}
import com.rxu.duoesports.service.TeamService
import com.rxu.duoesports.util.{ApiResponseHelpers, DuplicateTeamException}
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class TeamController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  teamService: TeamService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport
  with ApiResponseHelpers {

  def teams = silhouette.UserAwareAction { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.teams.teams(request.identity))
  }

  def createPage = silhouette.SecuredAction(CanCreateTeam()) { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.teams.createPage(request.identity))
  }

  def create = silhouette.SecuredAction(CanCreateTeam()).async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    CreateTeamForm.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid create team form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("account.changePassword.save.failure")))
      },
      createTeamForm => teamService.create(request.identity, createTeamForm) map { _ =>
        Ok("")
      } recover {
        case ex: DuplicateTeamException => ApiBadRequest(ex.msg)
      }
    )
  }

}
