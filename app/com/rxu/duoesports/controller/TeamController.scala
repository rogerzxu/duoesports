package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.dto.EditTeam
import com.rxu.duoesports.models.Role.Role
import com.rxu.duoesports.models.{Role, Team, User}
import com.rxu.duoesports.security.{BelongsToThisTeam, DefaultEnv}
import com.rxu.duoesports.service.{TeamService, UserService}
import com.rxu.duoesports.util.{ApiResponseHelpers, GetUserException, UpdateTeamException, UpdateUserException}
import com.rxu.duoesports.views.html
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

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
    verifyUserCanEditTeam(request.identity, name, api = false) { case(user, team) =>
      userService.getByTeamId(team.id) map { roster =>
        Ok(html.team.edit(user, team, roster))
      }
    }
  }

  def edit(name: String) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    verifyUserCanEditTeam(request.identity, name, api = true) { case(user, team) =>
      EditTeam.form.bindFromRequest.fold(
        badForm => {
          logger.error(s"Received invalid edit team form: ${badForm.toString}")
          Future.successful(ApiBadRequest(Messages("team.edit.failure")))
        },
        editForm => {
          val teamRoles = teamRolesMap(request.body.asFormUrlEncoded)
          teamService.update(team, editForm, teamRoles) map (_ => ApiOk(Messages("team.edit.success"))) recover {
            case ex @ (_: UpdateTeamException | _: UpdateUserException) =>
              logger.error(s"Failed to edit team $editForm", ex)
              ApiInternalError(Messages("team.edit.failure"))
            case ex: GetUserException =>
              logger.debug(s"Failed to edit team", ex)
              ApiBadRequest(ex.getMessage)
          }
        }
      )
    }
  }

  private def teamRolesMap(request: Option[Map[String, Seq[String]]]): Map[String, Role] = {
    request.map { form =>
      form.filter { case(key, value) =>
        val filterKeys = EditTeam.form.mapping.mappings map (_.key)
        !filterKeys.contains(key)
      } flatMap { case(key, value) =>
          val roleStr = value mkString ""
          if (roleStr == "Assign a Role") None
          else Try(Role.withName(roleStr)) match {
              case Success(userRole) => Some((key, userRole))
              case _ => None
            }
      }
    } getOrElse Map.empty
  }

  private def verifyUserCanEditTeam(user: User, teamName: String, api: Boolean)(canEdit: (User, Team) => Future[Result])
    (implicit request: SecuredRequest[DefaultEnv, AnyContent]): Future[Result] = {
    teamService.findByName(teamName) flatMap {
      case Some(team) => {
        if (user.canEditTeam(team.id)) {
          canEdit(user, team)
        } else {
          Future.successful {
            if (api) ApiUnauthorized()
            else Redirect(routes.HomeController.view().absoluteURL)
          }
        }
      }
      case None => Future.successful {
        if (api) ApiNotFound()
        else NotFound(html.errors.notFound(Some(user)))
      }
    }
  }

}

