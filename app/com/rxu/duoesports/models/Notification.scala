package com.rxu.duoesports.models

import anorm.{Macro, RowParser}
import com.rxu.duoesports.models.NotificationType.NotificationType

import java.time.LocalDateTime

case class Notification (
  id: Long,
  userId: Long,
  fromUser: Option[Long],
  notificationType: NotificationType,
  subject: String,
  body: String,
  unread: Boolean = true,
  createdAt: LocalDateTime = LocalDateTime.now(),
  updatedAt: LocalDateTime = LocalDateTime.now()
)

object Notification {
  val parser: RowParser[Notification] = Macro.namedParser[Notification]
}
