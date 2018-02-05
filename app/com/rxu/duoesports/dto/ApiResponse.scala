package com.rxu.duoesports.dto

import play.api.libs.json.{Format, JsArray, Json}

case class ApiResponse(
  status: String,
  message: String,
  data: Option[JsArray] = None
)

object ApiResponse {
  implicit val apiResponseFormat: Format[ApiResponse] = Json.format[ApiResponse]
}