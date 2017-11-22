package com.rxu.loliga.module

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.rxu.loliga.security.DefaultEnv
import com.rxu.loliga.service.UserService
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext.Implicits.global

class SecurityModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[EventBus].toInstance(EventBus())
  }

  @Provides
  def provideEnvironment(userService: UserService, authenticatorService: AuthenticatorService[CookieAuthenticator], eventBus: EventBus): Environment[DefaultEnv] = {
    Environment[DefaultEnv](userService, authenticatorService, Seq.empty, eventBus)
  }

}
