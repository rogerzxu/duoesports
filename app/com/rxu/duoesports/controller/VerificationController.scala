package com.rxu.duoesports.controller

import controllers.AssetsFinder

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.rxu.duoesports.dto.AddSummonerForm
import com.rxu.duoesports.security.DefaultEnv
import com.rxu.duoesports.service.VerificationService
import com.rxu.duoesports.util.{AddSummonerException, ApiResponseHelpers, UpdateUserException}
import com.typesafe.scalalogging.LazyLogging
import org.webjars.play.WebJarsUtil
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

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

  def addSummoner() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    AddSummonerForm.form.bindFromRequest.fold(
      badForm => {
        logger.error(s"Received invalid add summoner form: ${badForm.toString}")
        Future.successful(ApiBadRequest(Messages("account.summoner.add.failure")))
      },
      addSummonerForm => verificationService.addSummoner(request.identity, addSummonerForm) map { _ =>
        ApiOk(Messages("account.summoner.add.success"))
      } recover {
        case ex: AddSummonerException =>
          logger.warn(s"Failed to add summoner for ${request.identity.id}", ex)
          ApiBadRequest(ex.getMessage)
        case ex: UpdateUserException =>
          logger.error(s"Failed to add summoner for ${request.identity.id}", ex)
          ApiInternalError(Messages("account.summoner.add.failure"))
      }
    )
  }

}
