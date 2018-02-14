package com.rxu.duoesports.dto

import com.rxu.duoesports.models.Region
import com.rxu.duoesports.models.Region.Region
import play.api.data.Form
import play.api.data.Forms._

import play.api.data.Forms.mapping

case class AddSummonerForm(
  summonerName: String,
  region: String,
  verificationCode: String
) {
  def getRegion: Region = Region.withName(region)
}

object AddSummonerForm {

  val form = Form(
    mapping(
      "summonerName" -> nonEmptyText,
      "region" -> nonEmptyText,
      "verificationCode" -> nonEmptyText
    )(AddSummonerForm.apply)(AddSummonerForm.unapply)
  )

}
