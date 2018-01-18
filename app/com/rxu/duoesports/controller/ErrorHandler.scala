package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.{Inject, Provider}
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.{controllers, _}
import play.api.mvc.Results._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.RequestHeader
import play.api.routing.Router

import scala.concurrent.Future

class ErrorHandler @Inject()(
  val messagesApi: MessagesApi,
  env: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router]
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
  with LazyLogging
  with I18nSupport {

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    logger.error("Internal Server Error", exception)
    implicit val req: RequestHeader = request
    Future.successful(InternalServerError(com.rxu.duoesports.views.html.errors.internalServerError()))
  }

  override def onDevServerError(request: RequestHeader, exception: UsefulException) = {
    logger.error("Internal Server Error", exception)
    implicit val req: RequestHeader = request
    Future.successful(InternalServerError(com.rxu.duoesports.views.html.errors.internalServerError()))
  }

  override def onNotFound(request: RequestHeader, message: String) = {
    implicit val req: RequestHeader = request
    Future.successful(NotFound(com.rxu.duoesports.views.html.errors.notFound()))
  }

}
