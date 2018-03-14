package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.{AuthTokenService, UserService}
import com.rxu.duoesports.util.{ApiResponseHelpers, GetUserException}
import com.typesafe.scalalogging.LazyLogging
import com.rxu.duoesports.views.html
import org.webjars.play.WebJarsUtil
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class ResetPasswordController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
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

  def view = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(html.resetPassword.forgotPassword())
  }

  def sendResetPasswordEmail = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    sendResetPasswordForm.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid send reset password form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("forgotPassword.sendResetEmail.failure")))
      },
      sendResetPasswordForm => authTokenService.sendResetPasswordEmail(sendResetPasswordForm.email) map { _ =>
        ApiOk(Messages("forgotPassword.sendResetEmail.success"))
      } recover {
        case _ : GetUserException => ApiBadRequest("An account for this email was not found")
      }
    )
  }

  def resetPasswordPage(tokenId: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.findById(tokenId) map {
      case Some(authToken) if authToken.isValid => Ok(html.resetPassword.resetPassword(valid = true, tokenId))
      case _ => Ok(html.resetPassword.resetPassword(valid = false, tokenId))
    }
  }

  def resetPassword(tokenId: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.findById(tokenId) flatMap {
      case Some(authToken) if authToken.isValid => updatePasswordForm.bindFromRequest.fold(
        badForm => {
          logger.error(s"Received invalid update password form: ${badForm.toString}")
          Future.successful(ApiBadRequest(Messages("resetPassword.update.failure")))
        },
        updatePasswordForm => {
          for {
            user <- userService.getById(authToken.userId)
            loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            passwordInfo = passwordHasherRegistry.current.hash(updatePasswordForm.password)
            _ <- authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo)
          } yield {
            ApiOk(Messages("resetPassword.update.success"))
          }
        }
      )
      case _ => Future.successful(ApiBadRequest(Messages("resetPassword.update.failure")))
    }
  }

  case class UpdatePasswordForm(password: String)

  val updatePasswordForm = Form(
    mapping(
      "password" -> nonEmptyText
    )(UpdatePasswordForm.apply)(UpdatePasswordForm.unapply)
  )

  case class SendResetPasswordForm(email: String)

  val sendResetPasswordForm = Form(
    mapping(
      "email" -> email
    )(SendResetPasswordForm.apply)(SendResetPasswordForm.unapply)
  )

}
