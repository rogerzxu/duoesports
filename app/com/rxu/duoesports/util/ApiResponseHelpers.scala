package com.rxu.duoesports.util

import com.rxu.duoesports.dto.{ApiError, ApiResponse}
import play.api.libs.json._
import play.api.mvc.{Result, Results}

trait ApiResponseHelpers extends Results {

  def ApiUnauthorized: Result = {
    val response = ApiResponse(
      status = "failure",
      data = None,
      error = Some(ApiError(
        message = "You are not correctly authorized to use this API",
        errors = None
      ))
    )
    Unauthorized(Json.toJson(response))
  }

  def ApiOk: Result = {
    val response = ApiResponse(
      status = "success",
      data = None,
      error = None
    )
    Ok(Json.toJson(response))
  }

  def ApiOk[T](data: Seq[T])(implicit format: Format[T]): Result = {
    val response = ApiResponse(
      status = "success",
      data = Some(JsArray(data map (datum => Json.toJson(datum)))),
      error = None
    )
    Ok(Json.toJson(response))
  }

  def ApiOk[T](data: T)(implicit format: Format[T]): Result = {
    val response = ApiResponse(
      status = "success",
      data = Some(JsArray(Seq(Json.toJson(data)))),
      error = None
    )
    Ok(Json.toJson(response))
  }

  def ApiNotFound(message: String = "Not Found"): Result = {
    val response = ApiResponse(
      status = "failure",
      data = None,
      error = Some(ApiError(
        message = message,
        errors = None
      ))
    )
    NotFound(Json.toJson(response))
  }

  def ApiBadRequest(message: String = "Bad Request", errors: Seq[String] = Seq.empty): Result = {
    val response = ApiResponse(
      status = "failure",
      data = None,
      error = Some(ApiError(
        message = message,
        errors = if (errors.isEmpty) None else Some(errors)
      ))
    )
    BadRequest(Json.toJson(response))
  }

  def ApiInternalError(message: String = "Internal Error", errors: Seq[String] = Seq.empty): Result = {
    val response = ApiResponse(
      status = "failure",
      data = None,
      error = Some(ApiError(
        message = message,
        errors = if (errors.isEmpty) None else Some(errors)
      ))
    )
    InternalServerError(Json.toJson(response))
  }

  def ApiInvalidJson(jsonErrors: Seq[(JsPath, Seq[JsonValidationError])]): Result = {
    val errors = for {
      (jsPath, validationErrors) <- jsonErrors
      validationError <- validationErrors
      message <- validationError.messages
    } yield {
      s"${jsPath.toString}: $message"
    }
    val response = ApiResponse(
      status = "failure",
      data = None,
      error = Some(ApiError(
        message = "Json Validation Errors",
        errors = Some(errors)
      ))
    )
    BadRequest(Json.toJson(response))
  }

}