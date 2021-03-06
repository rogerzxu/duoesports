package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.controller.routes
import com.rxu.duoesports.models.{AuthToken, User}
import com.rxu.duoesports.service.dao.AuthTokenDao
import com.rxu.duoesports.util.ActivateUserException
import com.typesafe.scalalogging.LazyLogging
import play.api.mvc.RequestHeader

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AuthTokenService @Inject()(
  authTokenDao: AuthTokenDao,
  userService: UserService,
  mailerService: MailerService
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def sendActivationEmail(email: String)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    logger.info(s"Generating activation email for $email")
    userService.getByEmail(email) flatMap generateAndSendActivationEmail
  }

  def sendActivationEmail(userId: Long)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    logger.info(s"Generating activation email for $userId")
    userService.getById(userId) flatMap generateAndSendActivationEmail
  }

  private def generateAndSendActivationEmail(user: User)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    val token = AuthToken(UUID.randomUUID().toString, user.id, LocalDateTime.now().plusWeeks(2L))
    authTokenDao.upsert(token) map { _ =>
      val url = routes.ActivationController.activate(token.id).absoluteURL()
      mailerService.sendActivationEmail(user.email, url)
      (token, url)
    }
  }

  def sendResetPasswordEmail(email: String)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    logger.info(s"Generating reset password email for $email")
    for {
      user <- userService.getByEmail(email)
      token = AuthToken(UUID.randomUUID().toString, user.id, LocalDateTime.now().plusHours(1L))
      _ <- authTokenDao.upsert(token)
    } yield {
      val url = routes.ResetPasswordController.resetPasswordPage(token.id).absoluteURL()
      mailerService.sendResetPasswordEmail(user.email, url)
      (token, url)
    }
  }

  def findById(id: String): Future[Option[AuthToken]] = {
    authTokenDao.findById(id)
  }

  def activate(id: String): Future[Unit] = {
    logger.info(s"Activating AuthToken with id $id")
    for {
      maybeAuthToken <- authTokenDao.findById(id)
      authToken <- maybeAuthToken match {
        case Some(authToken) if authToken.isValid => Future.successful(authToken)
        case Some(authToken) => logger.error(s"Activation Token $authToken has already expired")
          Future.failed(ActivateUserException(s"Activation URL has already expired"))
        case None => logger.error(s"Cannot find token $id to activate")
          Future.failed(ActivateUserException(s"Cannot find token $id to Activate"))
      }
      _ <- userService.activate(authToken.userId)
      _ <- authTokenDao.deleteByUser(authToken.userId)
    } yield {
      logger.info(s"Deleting auth tokens for activated user ${authToken.userId}")
    }
  }

}
