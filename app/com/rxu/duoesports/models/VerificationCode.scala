package com.rxu.duoesports.models

import anorm.{Macro, RowParser}
import play.api.libs.json.{Format, Json}

case class VerificationCode(
  user_id: Long,
  code: String
)

object VerificationCode {
  implicit val verificationCodeFormat: Format[VerificationCode] = Json.format[VerificationCode]
  val parser: RowParser[VerificationCode] = Macro.namedParser[VerificationCode]
}
