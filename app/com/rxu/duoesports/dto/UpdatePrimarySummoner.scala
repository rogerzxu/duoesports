package com.rxu.duoesports.dto

import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}

case class UpdatePrimarySummoner(
  newPrimary: Option[String]
) {
  def getNewPrimarySummonerName: Option[String] = {
    newPrimary map { s =>
      s.trim.substring(0, s.length - 6)
    }
    //TODO: Fix if we support other regions
  }
}

object UpdatePrimarySummoner {

  val form = Form(
    mapping(
      "newPrimary" -> optional(text)
    )(UpdatePrimarySummoner.apply)(UpdatePrimarySummoner.unapply)
  )

}
