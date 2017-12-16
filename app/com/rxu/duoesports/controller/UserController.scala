package com.rxu.duoesports.controller

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.rxu.duoesports.service.UserService
import com.typesafe.scalalogging.LazyLogging
import play.api.i18n.I18nSupport
import play.api.libs.json.JsBoolean
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class UserController @Inject()(
  components: ControllerComponents,
  userService: UserService
)(
  implicit ec: ExecutionContext
) extends AbstractController(components)
with LazyLogging
with I18nSupport {

  def isUnique(email: String) = Action.async(parse.empty) { implicit request =>
    userService.retrieve(LoginInfo(CredentialsProvider.ID, email)) map {
      case Some(_) => Ok(JsBoolean(false))
      case None => Ok(JsBoolean(true))
    }
  }

}
