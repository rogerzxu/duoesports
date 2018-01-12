package com.rxu.duoesports.dto

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping

case class SignInData(
  email: String,
  password: String,
  rememberMe: String
) {
  def remember: Boolean = rememberMe equals "on"
}

object SignInForm {

  val form = Form(
    mapping(
      "signInEmail" -> email,
      "signInPassword" -> nonEmptyText,
      "rememberMe" -> text
    )(SignInData.apply)(SignInData.unapply)
  )

}
