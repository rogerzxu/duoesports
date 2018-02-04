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
    logger.info(s"Generating activation email for $email")
    userService.findByEmail(email) flatMap generateAndSendEmail
  }

  def generateAndSendEmail(userId: Long)(implicit req: RequestHeader): Future[(AuthToken, String)] = {
    logger.info(s"Generating activation email for $userId")
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
      case None =>
        logger.error("Cannot generate confirmation email for unknown user")
        throw GenerateAuthTokenException(s"Cannot generate confirmation email for unknown user.")
    }
  }

  def activate(id: String): Future[Unit] = {
    logger.info(s"Activating AuthToken with id $id")
    authTokenDao.findById(id) flatMap {
      case Some(authToken) => if (authToken.isValid) {
        userService.activate(authToken.user_id) flatMap {
          case 0 => logger.error(s"MariaDB failed to activate user $id")
            Future.failed(ActivateUserException(s"MariaDB failed to activate user $id"))
          case _ => for {
            _ <- authTokenDao.deleteByUser(authToken.user_id)
          } yield {
            logger.info(s"Deleting auth tokens for activated user ${authToken.user_id}")
          }
        }
      } else {
        logger.error(s"Activation Token $authToken has already expired")
        Future.failed(ActivateUserException(s"Activation URL has already expired"))
      }
      case None =>
        logger.error(s"Cannot find token $id to activate")
        Future.failed(ActivateUserException(s"Cannot find token $id to Activate"))
    }
  }

}
