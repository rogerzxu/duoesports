package com.rxu.loliga.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.rxu.loliga.security.DefaultEnv
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignUpController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(com.rxu.loliga.views.html.signUp()))
  }

}
