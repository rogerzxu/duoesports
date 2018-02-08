package com.rxu.duoesports.models

import anorm.{Macro, RowParser}

case class VerificationCode(
  user_id: Long,
  code: String
)

object VerificationCode {
  val parser: RowParser[VerificationCode] = Macro.namedParser[VerificationCode]
}
