package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.dto.EditTeam
import com.rxu.duoesports.models.{Team, User}
import com.rxu.duoesports.security.{BelongsToThisTeam, DefaultEnv}
import com.rxu.duoesports.service.{TeamService, UserService}
import com.rxu.duoesports.util.{ApiResponseHelpers, UpdateTeamException}
import com.rxu.duoesports.views.html
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Result}

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
    verifyUserCanEditTeam(request.identity, name) { case(user, team) =>
      Future.successful(Ok(html.team.edit(user, team)))
    }
  }

  def edit(name: String) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    verifyUserCanEditTeam(request.identity, name) { case(user, team) =>
      EditTeam.form.bindFromRequest.fold(
        badForm => {
          logger.error(s"Received invalid edit team form: ${badForm.toString}")
          Future.successful(ApiBadRequest(Messages("team.edit.failure")))
        },
        editForm => teamService.update(team, editForm) map (_ => ApiOk(Messages("team.edit.success"))) recover {
          case ex: UpdateTeamException =>
            logger.error(s"Failed to edit team $editForm", ex)
            ApiInternalError(Messages("team.edit.failure"))
        }
      )
    }
  }

  private def verifyUserCanEditTeam(user: User, teamName: String)(canEdit: (User, Team) => Future[Result])
    (implicit request: SecuredRequest[DefaultEnv, AnyContent]): Future[Result] = {
    teamService.findByName(teamName) flatMap {
      case Some(team) => {
        if (user.canEditTeam(team.id)) {
          canEdit(user, team)
        } else {
          Future.successful(Redirect(routes.HomeController.view().absoluteURL))
        }
      }
      case None => Future.successful(NotFound(html.errors.notFound(Some(user))))
    }
  }

}

