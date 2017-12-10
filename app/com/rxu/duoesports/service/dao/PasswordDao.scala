package com.rxu.duoesports.service.dao

import com.google.inject.Inject
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class PasswordDao @Inject()()(
  @Named("jdbcEC") implicit val executionContext: ExecutionContext
) extends DelegableAuthInfoDAO[PasswordInfo]{

  var data: mutable.HashMap[LoginInfo, PasswordInfo] = mutable.HashMap()

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    Future.successful(data.get(loginInfo))
  }

  override def add(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = {
    data += (loginInfo -> authInfo)
    Future.successful(authInfo)
  }

  override def update(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = {
    data += (loginInfo -> authInfo)
    Future.successful(authInfo)
  }

  override def save(
    loginInfo: LoginInfo,
    authInfo: PasswordInfo
  ): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None    => add(loginInfo, authInfo)
    }
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    data -= loginInfo
    Future.successful(())
  }
}
