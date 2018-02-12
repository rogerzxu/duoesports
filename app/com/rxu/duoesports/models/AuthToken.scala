package com.rxu.duoesports.models

import anorm.{Macro, RowParser}

import java.time.LocalDateTime

case class AuthToken (
  id: String,
  userId: Long,
  expiry: LocalDateTime
) {
  def isValid: Boolean = {
    expiry.compareTo(LocalDateTime.now) >= 0
  }
}

object AuthToken {
  val parser: RowParser[AuthToken] = Macro.namedParser[AuthToken]
}
