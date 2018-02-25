package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.security.{CanCreateTeam, DefaultEnv}
import com.rxu.duoesports.util.ApiResponseHelpers
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

class TeamController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
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

}
