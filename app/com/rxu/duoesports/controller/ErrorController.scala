package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.UserAwareRequest
import com.rxu.duoesports.security.DefaultEnv
import com.typesafe.scalalogging.LazyLogging
import com.rxu.duoesports.views.html
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

class ErrorController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport {

  def notFound = silhouette.UserAwareAction { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    Ok(html.errors.notFound(request.identity))
  }

  def internalServerError = silhouette.UserAwareAction { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    Ok(html.errors.internalServerError(request.identity))
  }

}

