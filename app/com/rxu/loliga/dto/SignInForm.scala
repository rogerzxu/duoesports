package com.rxu.loliga.dto

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping

case class SignInData(
  email: String,
  password: String,
  rememberMe: Boolean
)

object SignInForm {

  val form = Form(
    mapping(
      "signInEmail" -> email,
      "signInPassword" -> nonEmptyText,
      "rememberMe" -> boolean
    )(SignInData.apply)(SignInData.unapply)
  )

}
