package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.{Inject, Provider}
import com.rxu.duoesports.util.ApiResponseHelpers
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
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
  with I18nSupport
  with ApiResponseHelpers {

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    logger.error("PROD Internal Server Error", exception)
    implicit val req: RequestHeader = request
    Future.successful{
      request.contentType match {
        case Some(ct) if ct equalsIgnoreCase "application/json" => ApiInternalError(s"An internal error occurred: ${exception.getMessage}")
        case Some(ct) if ct equalsIgnoreCase "application/x-www-form-urlencoded" => InternalServerError(s"An internal error occurred: ${exception.getMessage}")
        case _ => Redirect(com.rxu.duoesports.controller.routes.ErrorController.internalServerError())
      }
    }
  }

  override def onDevServerError(request: RequestHeader, exception: UsefulException) = {
    logger.error("DEV Internal Server Error", exception)
    implicit val req: RequestHeader = request
    Future.successful{
      request.contentType match {
        case Some(ct) if ct equalsIgnoreCase "application/json" => ApiInternalError(s"An internal error occurred: ${exception.getMessage}")
        case Some(ct) if ct equalsIgnoreCase "application/x-www-form-urlencoded" => InternalServerError(s"An internal error occurred: ${exception.getMessage}")
        case _ => Redirect(com.rxu.duoesports.controller.routes.ErrorController.internalServerError())
      }
    }
  }

  override def onNotFound(request: RequestHeader, message: String) = {
    implicit val req: RequestHeader = request
    Future.successful{
      request.contentType match {
        case Some(ct) if ct equalsIgnoreCase "application/json" => ApiNotFound(s"Not found: $message")
        case Some(ct) if ct equalsIgnoreCase "application/x-www-form-urlencoded" => NotFound(s"Not found: $message")
        case _ => Redirect(com.rxu.duoesports.controller.routes.ErrorController.notFound())
      }
    }
  }

}
