package com.rxu.duoesports.dto

import play.api.libs.json.{Format, JsArray, Json}

case class ApiResponse(
  status: String,
  data: Option[JsArray],
  error: Option[ApiError]
)

case class ApiError(
  message: String,
  errors: Option[Seq[String]]
)

object ApiError {
  implicit val apiErrorFormat: Format[ApiError] = Json.format[ApiError]
}

object ApiResponse {
  implicit val apiResponseFormat: Format[ApiResponse] = Json.format[ApiResponse]
}