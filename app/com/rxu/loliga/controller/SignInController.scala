package com.rxu.loliga.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.rxu.loliga.config.AppConfig
import com.rxu.loliga.security.DefaultEnv
import com.rxu.loliga.service.UserService
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignInController @Inject()(
  appConfig: AppConfig,
  authInfoRepository: AuthInfoRepository,
  components: ControllerComponents,
  passwordHasherRegistry: PasswordHasherRegistry,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport {

  def signIn = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(""))
  }

}
