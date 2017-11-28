package com.rxu.loliga.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.loliga.dto.SignUpForm
import com.rxu.loliga.security.DefaultEnv
import com.rxu.loliga.service.UserService
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignUpController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
  userService: UserService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components) with LazyLogging {

  def view = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.loliga.views.html.signUp.signUp())
  }

  def signUpSuccess = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.loliga.views.html.signUp.signUpSuccess())
  }

  def signUpFailure = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.loliga.views.html.signUp.signUpFailure())
  }

  def signUp = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest.fold(
      form => {
        logger.debug(s"Received invalid sign-up form: ${form.toString}")
        Future.successful(Ok(com.rxu.loliga.views.html.signUp.signUp(Some("Internal Server Error"))))
      },
      signUpData => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, signUpData.email)
        userService.retrieve(loginInfo) flatMap {
          case Some(user) => Future.successful(Ok("asdf"))
          case None => Future.successful(Ok("asdf"))
        }
        Future.successful(Redirect(routes.SignUpController.signUpSuccess()))
      }
    )
  }

}
