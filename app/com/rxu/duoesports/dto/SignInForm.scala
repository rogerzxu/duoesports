package com.rxu.duoesports.dto

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping

case class SignInData(
  email: String,
  password: String,
  rememberMe: Option[String]
) {
  def remember: Boolean = rememberMe contains "on"
}

object SignInForm {

  val form = Form(
    mapping(
      "signInEmail" -> email,
      "signInPassword" -> nonEmptyText,
      "rememberMe" -> optional(text)
    )(SignInData.apply)(SignInData.unapply)
  )

}
