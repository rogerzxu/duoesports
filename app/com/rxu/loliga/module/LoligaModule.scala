package com.rxu.loliga.module

import com.google.inject.AbstractModule
import com.rxu.loliga.config.AppConfig
import net.codingwell.scalaguice.ScalaModule

class LoligaModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[AppConfig].asEagerSingleton()
  }

}
