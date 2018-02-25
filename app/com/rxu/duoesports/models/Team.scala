package com.rxu.duoesports.models

import anorm.{Macro, RowParser}
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.Role.Role

import java.time.LocalDateTime

case class Team(
  id: Long,
  name: String,
  region: Region,
  divisionId: Option[Long] = None,
  seasonId: Option[Long] = None,
  description: Option[String] = None,
  logoUrl: Option[String] = None,
  eligible: Boolean = true,
  isRecruiting: Boolean = false,
  recruitingRoles: Seq[Role] = Seq.empty,
  discordServer: Option[String] = None,
  updatedAt: LocalDateTime = LocalDateTime.now(),
  createdAt: LocalDateTime = LocalDateTime.now()
)

object Team {
  val parser: RowParser[Team] = Macro.namedParser[Team]
}
