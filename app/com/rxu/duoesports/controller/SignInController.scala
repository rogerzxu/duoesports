package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.api.{LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.{AccessDeniedException, IdentityNotFoundException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.duoesports.config.AppConfig
import com.rxu.duoesports.dto.SignInForm
import com.rxu.duoesports.models.User
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.UserService
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignInController @Inject()(
  appConfig: AppConfig,
  clock: Clock,
  components: ControllerComponents,
  credentialsProvider: CredentialsProvider,
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
    SignInForm.form.bindFromRequest.fold(
      signInForm => {
        logger.warn(s"Received invalid sign-up form: ${signInForm.toString}")
        Future.successful(BadRequest(Messages("signin.invalid.form")))
      },
      signInData => {
        val credentials = Credentials(signInData.email, signInData.password)
        (for {
          loginInfo <- credentialsProvider.authenticate(credentials)
          maybeUser <- userService.retrieve(loginInfo)
          user <- verifyUser(maybeUser)
          auth <- silhouette.env.authenticatorService.create(loginInfo)
          authenticator = if (signInData.remember) {
            auth.copy(
              expirationDateTime = clock.now + appConfig.authTtl,
              idleTimeout = appConfig.authIdleTimeout,
              cookieMaxAge = appConfig.authCookieMaxAge
            )
          } else auth
          authValue <- silhouette.env.authenticatorService.init(authenticator)
          result <- silhouette.env.authenticatorService.embed(authValue, Ok(Messages("signin.success")))
        } yield {
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          logger.info(s"User ${user.email} successfully logged in")
          result
        }) recover {
          case ex: AccessDeniedException =>
            logger.warn(s"User ${signInData.email} failed to login", ex)
            Unauthorized(Messages("signin.not.activated"))
          case ex: ProviderException =>
            logger.warn(s"User ${signInData.email} failed to login: ${ex.getMessage}")
            Unauthorized(Messages("signin.invalid.credentials"))
        }
      }
    )
  }

  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val result = Redirect(routes.HomeController.view())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  private def verifyUser(maybeUser: Option[User]): Future[User] = {
    maybeUser match {
      case Some(user) if !user.activated =>
        Future.failed(new AccessDeniedException(s"User ${user.email} has not been activated"))
        //TODO: resend activation email
      case Some(user) => Future.successful(user)
      case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
    }
  }

}
