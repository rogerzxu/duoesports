package com.rxu.duoesports.dto

import com.rxu.duoesports.models.Role
import com.rxu.duoesports.models.Role.Role
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}

case class EditTeam(
  logoUrl: Option[String],
  description: Option[String],
  recruiting: Option[String],
  top: Option[String],
  jungle: Option[String],
  mid: Option[String],
  bottom: Option[String],
  support: Option[String],
  coach: Option[String],
  analyst: Option[String],
  substitute: Option[String],
  discordServer: Option[String]
) {
  def getRecruitingRoles: Seq[Role] = {
    Seq(
      top map {_ => Role.Top},
      jungle map {_ => Role.Jungle},
      mid map {_ => Role.Middle},
      bottom map {_ => Role.Bottom},
      support map {_ => Role.Coach},
      coach map {_ => Role.Analyst},
      analyst map {_ => Role.Analyst},
      substitute map {_ => Role.Substitute}
    ).flatten
  }

  def isRecruiting: Boolean = recruiting.contains("on")
}

object EditTeam {

  val form = Form(
    mapping(
      "logoUrl" -> optional(text),
      "description" -> optional(text),
      "isRecruiting" -> optional(text),
      "top" -> optional(text),
      "jungle" -> optional(text),
      "mid" -> optional(text),
      "bottom" -> optional(text),
      "support" -> optional(text),
      "coach" -> optional(text),
      "analyst" -> optional(text),
      "substitute" -> optional(text),
      "discordServer" -> optional(text)
    )(EditTeam.apply)(EditTeam.unapply)
  )

}