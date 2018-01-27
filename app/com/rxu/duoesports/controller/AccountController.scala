package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.{AuthTokenService, UserService}
import com.rxu.duoesports.util.ActivateUserException
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
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

  def activate(tokenId: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.activate(tokenId) map (_ => Ok(com.rxu.duoesports.views.html.account.activation())) recover {
      case ex: ActivateUserException => {
        val resendUrl = request.cookies.get("duoesportsEmail") map { cookie =>
          routes.AccountController.sendActivationEmail(email = cookie.value).absoluteURL
        }
        BadRequest(com.rxu.duoesports.views.html.account.activation(Some(ex.getMessage + "."), resendUrl))
      }
    }
  }

  def sendActivationEmail(email: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.generateAndSendEmail(email) map (_ => Ok(Messages("resend.activation.success"))) recover {
      case ex: Throwable => InternalServerError(Messages("resend.activation.failure") + s" ${ex.getMessage}")
    }
  }

}
