package com.rxu.duoesports.util

import com.rxu.duoesports.models.Rank.Rank
import com.rxu.duoesports.models.Role.Role
import com.rxu.duoesports.models.{Role, User}
import play.api.mvc.RequestHeader
import play.twirl.api.Html

object TemplateHelpers {

  val discordIcon = "https://s3.amazonaws.com/duoesports-images/icons/discord.svg"
  val missingProfileImage = "https://s3.amazonaws.com/duoesports-images/icons/missing-profile-photo.png"
  val captainIcon = "https://s3.amazonaws.com/duoesports-images/icons/captain.svg"
  val captainEmptyIcon = "https://s3.amazonaws.com/duoesports-images/icons/captain_empty.png"
  val reportIcon = "https://s3.amazonaws.com/duoesports-images/icons/report.png"
  val questionIcon = "https://s3.amazonaws.com/duoesports-images/icons/question.png"
  val applyIcon = "https://s3.amazonaws.com/duoesports-images/icons/apply.png"
  val deleteIcon = "https://s3.amazonaws.com/duoesports-images/icons/delete.png"

  def getRankIconUrl(rank: Rank): String = {
    s"https://s3.amazonaws.com/duoesports-images/leagueIcons/${rank.toString.replaceAll(" ", "_").toLowerCase}.png"
  }

  def getRoleIcon(role: Role): String = {
    role match {
      case Role.Substitute => "https://s3.amazonaws.com/duoesports-images/icons/sub_icon.png"
      case Role.Analyst => "https://s3.amazonaws.com/duoesports-images/icons/coach_analyst_icon.png"
      case Role.Coach => "https://s3.amazonaws.com/duoesports-images/icons/coach_analyst_icon.png"
      case r: Role => s"https://s3.amazonaws.com/duoesports-images/icons/${r.toString.toLowerCase}_icon.png"
    }
  }

  def getTeamListNavigation(currentPage: Int, teamCount: Int, queryString: String)
    (implicit requestHeader: RequestHeader): Html = {
    val totalPages = teamCount / 10 + 1
    val stringBuilder = StringBuilder.newBuilder
    for(i <- 1 to totalPages) {
      if(currentPage == i) {
        stringBuilder.append(s"""<li class=active>$i</li>""")
      } else {
        if (queryString.isEmpty) {
          stringBuilder.append(s"""<li><a href="?page=$i">$i</a></li>""")
        } else if (queryString contains "page=") {
          val qs = queryString.replaceFirst(s"page=$currentPage", s"page=$i")
          stringBuilder.append(s"""<li><a href="?$qs">$i</a></li>""")
        } else {
          stringBuilder.append(s"""<li><a href="?page=$i&$queryString">$i</a></li>""")
        }
      }
    }
    Html(stringBuilder.toString)
  }

  def canCreateTeam(maybeUser: Option[User]): Boolean = {
    maybeUser exists { _.canJoinTeam}
  }

}
