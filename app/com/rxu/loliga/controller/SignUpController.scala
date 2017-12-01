package com.rxu.loliga.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.loliga.dto.SignUpForm
import com.rxu.loliga.models.{Region, Roles, User}
import com.rxu.loliga.security.DefaultEnv
import com.rxu.loliga.service.UserService
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import java.util.UUID
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
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport {

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
        Future.successful(Ok(com.rxu.loliga.views.html.signUp.signUp(Some(Messages("signup.failure")))))
      },
      signUpData => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, signUpData.email)
        userService.retrieve(loginInfo) flatMap {
          case Some(_) => Future.successful(Redirect(routes.SignUpController.signUpFailure()))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(signUpData.password)
            val user = User(
              userId = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = signUpData.firstName,
              lastName = signUpData.lastName,
              email = signUpData.email,
              summonerName = signUpData.summonerName,
              region = Region.withName(signUpData.region),
              role = Roles.Player,
              activated = true
            )
            for {
              _ <- userService.save(user)
              _ <- authInfoRepository.add(loginInfo, authInfo)
            } yield {
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              Redirect(routes.SignUpController.signUpSuccess())
            }
        }
      }
    )
  }

}
