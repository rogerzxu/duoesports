package com.rxu.duoesports.config

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.FiniteDuration

class AppConfig extends LazyLogging {
  val c = ConfigFactory.load

  val authTtl = c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry")
  val authIdleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
  val authCookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")

  val jdbcMaxThreads = c.getInt("jdbc.maxThreads")
  val mariaDbUrl = c.getString("db.default.url")

  val riotApiKey = c.getString("riot.api.key")
  val riotBaseUrl = c.getString("riot.api.baseUrl")

  logConfig()

  def logConfig() = {
    logger.info(s"Authentication TTL: $authTtl")
    logger.info(s"Authentication Idle Timeout: $authIdleTimeout")
    logger.info(s"Authentication Cookie Max Age: $authCookieMaxAge")
    logger.info(s"JDBC Max Threads: $jdbcMaxThreads")
    logger.info(s"MariaDB URL: $mariaDbUrl")
    logger.info(s"RIOT API base url: $riotBaseUrl")
  }
}
