package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.duoesports.dto.{ChangePasswordForm, UpdateAccountInfoForm, UpdatePlayerInfo}
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
  userService: UserService,
  passwordHasherRegistry: PasswordHasherRegistry,
  credentialsProvider: CredentialsProvider,
  authInfoRepository: AuthInfoRepository
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

  def updateAccountInfo() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    UpdateAccountInfoForm.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid account info form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("account.accountInfo.save.failure")))
      },
      updateAccountInfo => userService.update(request.identity, updateAccountInfo) map (_ => ApiOk(Messages("account.accountInfo.save.success"))) recover {
        case _: UpdateUserException => ApiInternalError(Messages("account.accountInfo.save.failure"))
      }
    )
  }

  def changePasswordPage = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.changePassword(request.identity))
  }

  def changePassword() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    ChangePasswordForm.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid change password form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("account.changePassword.save.failure")))
      },
      changePasswordData => {
        val credentials = Credentials(request.identity.email, changePasswordData.currentPassword)
        (for {
          loginInfo <- credentialsProvider.authenticate(credentials)
          passwordInfo =  passwordHasherRegistry.current.hash(changePasswordData.password)
          _ <- authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo)
        } yield {
          logger.info(s"User ${request.identity.email} successfully changed password")
          ApiOk(Messages("account.changePassword.save.success"))
        }) recover {
          case ex: ProviderException => {
            logger.warn(s"User ${request.identity.email} failed to change password: ${ex.getMessage}")
            ApiBadRequest(Messages("account.changePassword.save.invalidCurrentPassword"))
          }
        }
      }
    )
  }

  def player = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.player(request.identity))
  }

  def updatePlayer() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    UpdatePlayerInfo.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid player info form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("account.playerInfo.save.failure")))
      },
      updatePlayerInfo => userService.update(request.identity, updatePlayerInfo) map (_ => ApiOk(Messages("account.playerInfo.save.success"))) recover {
        case _: UpdateUserException => ApiInternalError(Messages("account.playerInfo.save.failure"))
      }
    )
  }

  def lolAccount = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.lolAccount(request.identity))
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
    authTokenService.generateAndSendEmail(email) map (_ => ApiOk(Messages("resend.activation.success"))) recover {
      case ex: Throwable => ApiInternalError(Messages("resend.activation.failure") + s" ${ex.getMessage}")
    }
  }

}
