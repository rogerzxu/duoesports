package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.dto.UpdateAccountInfoForm
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.{AuthTokenService, UserService}
import com.rxu.duoesports.util.{ActivateUserException, ApiResponseHelpers, UpdateUserException}
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

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
  with I18nSupport
  with ApiResponseHelpers {

  def account = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.account(request.identity))
  }

  def changePasswordPage = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.password(request.identity))
  }

  def player = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.player(request.identity))
  }

  def lolAccount = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.lolAccount(request.identity))
  }

  def updateAccountInfo() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    UpdateAccountInfoForm.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid account info form: ${badForm.toString}")
        Future.successful(BadRequest(Messages("account.accountInfo.save.failure")))
      },
      updateAccountInfo => userService.update(request.identity.id.get, updateAccountInfo) map (_ => Ok(Messages("account.accountInfo.save.success"))) recover {
        case ex: UpdateUserException => InternalServerError(Messages("account.accountInfo.save.failure"))
      }
    )
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
