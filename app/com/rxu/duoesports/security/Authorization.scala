package com.rxu.duoesports.security

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.rxu.duoesports.models.User
import play.api.mvc.Request

import scala.concurrent.Future

case class CanCreateTeam() extends Authorization[User, CookieAuthenticator] {
  override def isAuthorized[B](
    user: User,
    authenticator: CookieAuthenticator
  )(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(user.isTeamless && user.verified)
  }
}
