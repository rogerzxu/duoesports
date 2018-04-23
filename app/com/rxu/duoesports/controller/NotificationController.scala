package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.util.ApiResponseHelpers
import com.rxu.duoesports.views.html
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

class NotificationController @Inject()(
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

  def notifications = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(html.notification.notifications(request.identity))
  }

}
