package com.rxu.duoesports.service

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.mailer.{Email, MailerClient}

class MailerService @Inject()(
  mailerClient: MailerClient
) extends LazyLogging {

  def sendActivationEmail(toEmail: String, url: String): String = {
    val email = Email(
      subject = "Duoesports - Activate Your Account",
      from = "support@duoesports.com",
      to = Seq(toEmail),
      bodyText = Some(url)
    )
    logger.info(s"Sending activation email $email")
    mailerClient.send(email)
  }

}
