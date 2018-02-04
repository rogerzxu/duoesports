package com.rxu.duoesports.dto

import play.api.data.Form
import play.api.data.Forms.{mapping, _}

case class ChangePasswordData(
  currentPassword: String,
  password: String
)

object ChangePasswordForm {

  val form = Form(
    mapping(
      "currentPassword" -> nonEmptyText,
      "password" -> nonEmptyText
    )(ChangePasswordData.apply)(ChangePasswordData.unapply)
  )

}
