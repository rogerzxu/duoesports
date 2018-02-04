package com.rxu.duoesports.dto

import com.rxu.duoesports.models.Timezone
import com.rxu.duoesports.models.Timezone.Timezone
import play.api.data.Form
import play.api.data.Forms._

case class UpdateAccountInfo(
  firstName: String,
  lastName: String,
  timezone: String
) {
  def getTimezone: Timezone = {
    timezone match {
      case "Eastern" => Timezone.EASTERN
      case "Central" => Timezone.CENTRAL
      case "Mountain" => Timezone.MOUNTAIN
      case "Pacific" => Timezone.PACIFIC
      case "Alaska" => Timezone.ALASKA
      case "Hawaii" => Timezone.HAWAII
      case _ => Timezone.EASTERN
    }
  }
}

object UpdateAccountInfoForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "timezone" -> nonEmptyText
    )(UpdateAccountInfo.apply)(UpdateAccountInfo.unapply)
  )

}
