package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.models.AuthToken
import com.rxu.duoesports.service.dao.AuthTokenDao
import com.rxu.duoesports.util.ActivateUserException
import com.typesafe.scalalogging.LazyLogging

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AuthTokenService @Inject()(
  authTokenDao: AuthTokenDao,
  userService: UserService
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def create(userId: Long): Future[AuthToken] = {
    val token = AuthToken(UUID.randomUUID().toString, userId, LocalDateTime.now().plusWeeks(2L))
    authTokenDao.upsert(token) map (_ => token)
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
      case None => Future.failed(ActivateUserException(s"Cannot find user $id to Activate"))
    }
  }

}
