package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.models.{Notification, NotificationType}
import com.rxu.duoesports.service.dao.NotificationDao
import com.typesafe.scalalogging.LazyLogging
import play.api.i18n.Messages

import scala.concurrent.{ExecutionContext, Future}

class NotificationService @Inject()(
  notificationDao: NotificationDao
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def getByUserId(userId: Long): Future[Seq[Notification]] = {
    logger.debug(s"Getting notifications for user $userId")
    notificationDao.getByUserId(userId)
  }

  /*def sendDisbandNotification(userId: Long): Future[Long] = {
    logger.info(s"Sending disband notification to $userId")
    val notification = Notification(
      id = 0L,
      userId = userId,
      fromUser = None,
      notificationType = NotificationType.SYSTEM,
      subject = Messages("notifications.disband.subject"),
      body = Messages("notification.disband.body")
    )
    notificationDao.create(notification)
  }*/

}
