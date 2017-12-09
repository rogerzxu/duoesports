package com.rxu.duoesports.security

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.rxu.duoesports.models.User

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
