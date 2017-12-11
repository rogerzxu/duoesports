package com.rxu.duoesports.service.dao

import anorm._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.rxu.duoesports.util.SavePasswordException
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class PasswordDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val executionContext: ExecutionContext
) extends DelegableAuthInfoDAO[PasswordInfo] {

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    db.withConnection { implicit c =>
      val password = SQL(
        s"""
           SELECT password FROM User
           WHERE email = {email}
        """
      ).on('email -> loginInfo.providerKey).as(SqlParser.str("password").singleOpt)
      password map (PasswordInfo(BCryptSha256PasswordHasher.ID, _))
    }
  }

  override def add(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = save(loginInfo, authInfo)

  override def update(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = save(loginInfo, authInfo)

  override def save(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = Future {
    db.withTransaction { implicit c =>
      val maybePass = SQL(
        s"""
          SELECT password FROM User
          WHERE email = {email}
          FOR UPDATE
        """
      ).on('email -> loginInfo.providerKey).as(SqlParser.str("password").singleOpt)
      maybePass match {
        case Some(_) =>
          SQL(s"UPDATE User SET password = {password}").on('password -> authInfo.password).executeUpdate()
          authInfo
        case None =>
          throw SavePasswordException("Cannot save password for non-existing User")
      }
    }
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    //TODO: implement
    Future.successful(())
  }
}
