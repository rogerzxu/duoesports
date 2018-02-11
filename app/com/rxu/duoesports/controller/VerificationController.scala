package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.VerificationService
import com.rxu.duoesports.util.ApiResponseHelpers
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

class VerificationController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  verificationService: VerificationService
)(
  implicit webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components)
  with LazyLogging
  with I18nSupport
  with ApiResponseHelpers {

  def addSummonerPage() = silhouette.SecuredAction { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Ok(com.rxu.duoesports.views.html.account.addSummoner(request.identity))
  }

  def generateVerificationCode() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    verificationService.generateVerificationCode(request.identity) map { code =>
      ApiOk(code)
    } recover {
      case ex: Throwable =>
        logger.error(s"Failed to generate verification code for ${request.identity.id}", ex)
        ApiInternalError(Messages("account.addSummoner.generateCode.failure"))
    }
  }

}
