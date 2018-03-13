package com.rxu.duoesports.dto

import com.rxu.duoesports.models.Region
import com.rxu.duoesports.models.Region.Region
import play.api.data.Form
import play.api.data.Forms.{mapping, _}

case class CreateTeamForm(
  region: String,
  teamName: String
) {
  def getRegion: Region = Region.withName(region)
}

object CreateTeamForm {

  val form = Form(
    mapping(
      "region" -> nonEmptyText,
      "teamName" -> nonEmptyText
    )(CreateTeamForm.apply)(CreateTeamForm.unapply)
  )

}
