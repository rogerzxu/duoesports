package com.rxu.duoesports.security

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import javax.inject.Inject
import scala.concurrent.Future

class DuoesportsSecuredHandler @Inject() (val messagesApi: MessagesApi) extends SecuredErrorHandler with I18nSupport {

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: RequestHeader) = {
    Future.successful(Redirect(com.rxu.duoesports.controller.routes.HomeController.view()))
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Redirect(com.rxu.duoesports.controller.routes.HomeController.view()))
  }
}
