package com.rxu.loliga.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.rxu.loliga.dto.SignUpForm
import com.rxu.loliga.security.DefaultEnv
import org.webjars.play.WebJarsUtil
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{ExecutionContext, Future}

class SignUpController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components) {

  def view = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(com.rxu.loliga.views.html.signUp())
  }

  def signUp = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val form = request.body.asFormUrlEncoded
    println(form)
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(form.toString)),
      signUpData => Future.successful(Ok(signUpData.toString))
    )
  }

}
