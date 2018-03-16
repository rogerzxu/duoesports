package com.rxu.duoesports.util

import com.typesafe.scalalogging.LazyLogging
import play.api.cache.AsyncCacheApi

import scala.concurrent.{ExecutionContext, Future}

trait CacheHelpers extends LazyLogging {

  def cacheGetOrPut[T](cache: AsyncCacheApi, cacheKey: String, finder: Future[Option[T]])
    (implicit executionContext: ExecutionContext): Future[Option[T]] = {
    cache.get[Option[T]](cacheKey) flatMap {
      case Some(cachedValue) =>
        logger.trace(s"Found entry in cache for ${cacheKey}")
        Future.successful(cachedValue)
      case None => finder map { value =>
        logger.trace(s"Storing entry in cache for ${cacheKey}")
        cache.set(cacheKey, value)
        value
      }
    }
  }

}
