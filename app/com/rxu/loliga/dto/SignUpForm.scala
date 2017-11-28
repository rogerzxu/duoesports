package com.rxu.loliga.dto

import play.api.data.Form
import play.api.data.Forms._

case class SignUpData(
  email: String,
  firstName: String,
  lastName: String,
  summonerName: String,
  region: String,
  password: String
)

object SignUpForm {

  val form = Form(
    mapping(
      "email" -> email,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "summonerName" -> nonEmptyText,
      "region" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignUpData.apply)(SignUpData.unapply)
  )

}
