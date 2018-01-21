package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.controller.routes
import com.rxu.duoesports.models.{AuthToken, User}
import com.rxu.duoesports.service.dao.AuthTokenDao
import com.rxu.duoesports.util.{ActivateUserException, GenerateAuthTokenException}
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

  def generateAndSendEmail(email: String)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    userService.findByEmail(email) flatMap generateAndSendEmail
  }

  def generateAndSendEmail(userId: Long)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    userService.findById(userId) flatMap generateAndSendEmail
  }

  private def generateAndSendEmail(maybeUser: Option[User])(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    maybeUser match {
      case Some(user) => {
        val token = AuthToken(UUID.randomUUID().toString, user.id.get, LocalDateTime.now().plusWeeks(2L))
        authTokenDao.upsert(token) map { _ =>
          val url = routes.AccountController.activate(token.id).absoluteURL()
          mailerService.sendActivationEmail(user.email, url)
          (token, url)
        }
      }
      case None => throw GenerateAuthTokenException(s"Cannot generate confirmation email for unknown user.")
    }
  }

  def activate(id: String): Future[Unit] = {
    logger.info(s"Activating AuthToken with id $id")
    authTokenDao.findById(id) flatMap {
      case Some(authToken) => if (authToken.isValid) {
        userService.activate(authToken.user_id)
      } else {
        Future.failed(ActivateUserException(s"Activation URL has already expired"))
        //TODO: "send email again"
      }
      case None => Future.failed(ActivateUserException(s"Cannot find token $id to Activate"))
    }
  }

}
