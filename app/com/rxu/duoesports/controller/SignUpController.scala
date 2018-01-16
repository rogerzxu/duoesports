package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.duoesports.dto.SignUpForm
import com.rxu.duoesports.models.{User, UserRole}
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.{AuthTokenService, MailerService, UserService}
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignUpController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerService: MailerService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport {

  def view = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.duoesports.views.html.signUp.signUp())
  }

  def signUpSuccess = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.duoesports.views.html.signUp.signUpSuccess())
  }

  def signUp = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest.fold(
      form => {
        logger.warn(s"Received invalid sign-up form: ${form.toString}")
        Future.successful(BadRequest(Messages("signup.failure")))
      },
      signUpData => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, signUpData.email)
        userService.retrieve(loginInfo) flatMap {
          case Some(_) => Future.successful(Conflict(Messages("signup.duplicate.email")))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(signUpData.password)
            val user = User(
              id = None,
              email = signUpData.email,
              password = authInfo.password,
              firstName = signUpData.firstName,
              lastName = signUpData.lastName,
              user_role = UserRole.Player
            ).normalize
            for {
              userId <- userService.create(user)
              _ <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(userId)
            } yield {
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              mailerService.sendActivationEmail(user.email, routes.AccountController.activate(authToken.id).absoluteURL())
              Ok(Messages("signup.success"))
            }
        }
      }
    )
  }

}
