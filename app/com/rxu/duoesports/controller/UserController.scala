package com.rxu.duoesports.controller

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.UserService
import com.typesafe.scalalogging.LazyLogging
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService
)(
  implicit ec: ExecutionContext
) extends AbstractController(components)
with LazyLogging
with I18nSupport {

  def getVerificationCode = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    userService.getVerificationCode(request.identity.email) map { code =>
      Ok(Json.obj("verification_code" -> code))
    }
  }

}
