package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.{AuthTokenService, UserService}
import com.rxu.duoesports.util.ActivateUserException
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class AccountController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authTokenService: AuthTokenService,
  userService: UserService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport {

  def account = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.account(request.identity))
  }

  def getVerificationCode = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    userService.getVerificationCode(request.identity.email) map { code =>
      Ok(Json.obj("verification_code" -> code))
    }
  }

  def activationSuccess = silhouette.UserAwareAction { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.activationSuccess())
  }

  def activationFailure = silhouette.UserAwareAction { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.activationSuccess())
  }

  def activate(tokenId: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.activate(tokenId) map (_ => Redirect(routes.AccountController.activationSuccess())) recover {
      case ex: ActivateUserException => Redirect(routes.AccountController.activationFailure())
    }
  }

}
