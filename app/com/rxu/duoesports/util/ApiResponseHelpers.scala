package com.rxu.duoesports.util

import com.rxu.duoesports.dto.ApiResponse
import play.api.libs.json._
import play.api.mvc.{Result, Results}

trait ApiResponseHelpers extends Results {

  def ApiOk(message: String = "success"): Result = {
    val response = ApiResponse(
      status = "success",
      message = message
    )
    Ok(Json.toJson(response))
  }

  def ApiOk[T](data: Seq[T])(implicit format: Format[T]): Result = {
    val response = ApiResponse(
      status = "success",
      message = "success",
      data = Some(JsArray(data map (datum => Json.toJson(datum))))
    )
    Ok(Json.toJson(response))
  }

  def ApiOk[T](data: T)(implicit format: Format[T]): Result = {
    val response = ApiResponse(
      status = "success",
      message = "success",
      data = Some(JsArray(Seq(Json.toJson(data))))
    )
    Ok(Json.toJson(response))
  }

  def ApiUnauthorized(message: String = "Unauthorized"): Result = {
    val response = ApiResponse(
      status = "failure",
      message = message
    )
    Unauthorized(Json.toJson(response))
  }

  def ApiNotFound(message: String = "Not Found"): Result = {
    val response = ApiResponse(
      status = "failure",
      message = message
    )
    NotFound(Json.toJson(response))
  }

  def ApiBadRequest(message: String = "Bad Request"): Result = {
    val response = ApiResponse(
      status = "failure",
      message = message
    )
    BadRequest(Json.toJson(response))
  }

  def ApiInternalError(message: String = "Internal Error", errors: Seq[String] = Seq.empty): Result = {
    val response = ApiResponse(
      status = "failure",
      message = message
    )
    InternalServerError(Json.toJson(response))
  }

}