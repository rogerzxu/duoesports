package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.dto.CreateTeamForm
import com.rxu.duoesports.models.Role
import com.rxu.duoesports.models.Role.Role
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

  def teams(
    page: Option[Int] = None,
    search: Option[String] = None,
    isRecruiting: Option[String] = None,
    top: Option[String] = None,
    jungle: Option[String] = None,
    middle: Option[String] = None,
    bottom: Option[String] = None,
    support: Option[String] = None,
    coach: Option[String] = None,
    analyst: Option[String] = None
  ) = silhouette.UserAwareAction.async { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    val rolesFilter = getRolesToFilter(top, jungle, middle, bottom, support, coach, analyst)
    val recruiting = (isRecruiting contains "on") || rolesFilter.nonEmpty
    val searchQ = search flatMap { s =>
      if (s.isEmpty) None
      else Some(s)
    }
      for {
        teams <- teamService.searchByNamesPaginated(
          page.getOrElse(1),
          search = searchQ,
          onlyRecruiting = recruiting,
          rolesFilter = rolesFilter
        )
        teamCount <- teamService.getCount(searchQ, recruiting, rolesFilter)
      } yield {
        Ok(com.rxu.duoesports.views.html.teams.teams(
          user = request.identity,
          teams = teams,
          pageNumber = page.getOrElse(1),
          teamCount = teamCount,
          queryString = request.rawQueryString,
          search = search,
          isRecruiting = recruiting,
          rolesFilter = rolesFilter
        ))
      }
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

  private def getRolesToFilter(
    top: Option[String] = None,
    jungle: Option[String] = None,
    middle: Option[String] = None,
    bottom: Option[String] = None,
    support: Option[String] = None,
    coach: Option[String] = None,
    analyst: Option[String] = None
  ): Seq[Role] = {
    Seq(
      top match {
        case Some(on) if on == "on" => Some(Role.Top)
        case _ => None
      },
      jungle match {
        case Some(on) if on == "on" => Some(Role.Jungle)
        case _ => None
      },
      middle match {
        case Some(on) if on == "on" => Some(Role.Middle)
        case _ => None
      },
      bottom match {
        case Some(on) if on == "on" => Some(Role.Bottom)
        case _ => None
      },
      support match {
        case Some(on) if on == "on" => Some(Role.Support)
        case _ => None
      },
      coach match {
        case Some(on) if on == "on" => Some(Role.Coach)
        case _ => None
      },
      analyst match {
        case Some(on) if on == "on" => Some(Role.Analyst)
        case _ => None
      }
    ).flatten
  }

}
