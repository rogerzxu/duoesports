package com.rxu.duoesports.util

import com.rxu.duoesports.models.Rank.Rank
import com.rxu.duoesports.models.User

object TemplateHelpers {

  def getRankIconUrl(rank: Rank): String = {
    s"https://s3.amazonaws.com/duoesports-images/icons/${rank.toString.replaceAll(" ", "_").toLowerCase}.png"
  }

  def canCreateTeam(maybeUser: Option[User]): Boolean = {
    maybeUser exists { user =>
      user.isTeamless && user.verified
    }
  }

}
