package com.rxu.duoesports.service

import com.google.inject.Inject
import play.api.libs.mailer.{Email, MailerClient}

class MailerService @Inject()(
  mailerClient: MailerClient
){

  def sendActivationEmail(toEmail: String, url: String): String = {
    val email = Email(
      subject = "Duoesports - Activate Your Account",
      from = "support@duoesports.com",
      to = Seq(toEmail),
      bodyText = Some(url)
    )
    mailerClient.send(email)
  }

}
