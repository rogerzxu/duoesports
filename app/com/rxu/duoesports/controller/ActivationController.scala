package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.AuthTokenService
import com.rxu.duoesports.util.{ActivateUserException, ApiResponseHelpers}
import com.rxu.duoesports.views.html
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class ActivationController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authTokenService: AuthTokenService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport
  with ApiResponseHelpers {

  def activate(tokenId: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.activate(tokenId) map (_ => Ok(html.activation())) recover {
      case ex: ActivateUserException=> {
        val resendUrl = request.cookies.get("duoesportsEmail") map { cookie =>
          routes.ActivationController.sendActivationEmail(email = cookie.value).absoluteURL
        }
        BadRequest(html.activation(Some(ex.getMessage + "."), resendUrl))
      }
    }
  }

  def sendActivationEmail(email: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.sendActivationEmail(email) map (_ => ApiOk(Messages("resend.activation.success"))) recover {
      case ex: Throwable =>
        logger.error(s"Failed to send activation email to $email", ex)
        ApiInternalError(Messages("resend.activation.failure") + s" ${ex.getMessage}")
    }
  }

}
