package com.rxu.duoesports.dto

import com.rxu.duoesports.models.Role
import com.rxu.duoesports.models.Role.Role
import play.api.data.Form
import play.api.data.Forms._

case class UpdatePlayerInfo(
  profileImage: Option[String],
  description: Option[String],
  top: Option[String],
  jungle: Option[String],
  mid: Option[String],
  bottom: Option[String],
  support: Option[String],
  coach: Option[String],
  analyst: Option[String],
  discordId: Option[String]
) {
  def getRoles: Seq[Role] = {
    Seq(
      top map {_ => Role.Top},
      jungle map {_ => Role.Jungle},
      mid map {_ => Role.Middle},
      bottom map {_ => Role.Bottom},
      support map {_ => Role.Coach},
      coach map {_ => Role.Analyst},
      analyst map {_ => Role.Analyst}
    ).flatten
  }
}

object UpdatePlayerInfo {

  val form = Form(
    mapping(
      "profileImage" -> optional(text),
      "description" -> optional(text),
      "top" -> optional(text),
      "jungle" -> optional(text),
      "mid" -> optional(text),
      "bottom" -> optional(text),
      "support" -> optional(text),
      "coach" -> optional(text),
      "analyst" -> optional(text),
      "discordId" -> optional(text)
    )(UpdatePlayerInfo.apply)(UpdatePlayerInfo.unapply)
  )

}
