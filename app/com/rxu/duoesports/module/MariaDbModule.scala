package com.rxu.duoesports.module

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.rxu.duoesports.config.AppConfig
import net.codingwell.scalaguice.ScalaModule

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class MariaDbModule extends AbstractModule with ScalaModule {

  @Provides
  @Singleton
  @Named("jdbcEC")
  def getSearchEC(appConfig: AppConfig): ExecutionContext = {
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(appConfig.jdbcMaxThreads))
  }

  override def configure(): Unit = {}
}
