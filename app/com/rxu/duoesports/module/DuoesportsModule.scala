package com.rxu.duoesports.module

import com.google.inject.AbstractModule
import com.rxu.duoesports.config.AppConfig
import net.codingwell.scalaguice.ScalaModule

class DuoesportsModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[AppConfig].asEagerSingleton()
  }

}
