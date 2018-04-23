package com.rxu.duoesports.service.dao

import anorm.SQL
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.rxu.duoesports.models.Notification
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def getByUserId(userId: Long): Future[Seq[Notification]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           SELECT * FROM Notification
           where userId = {userId}
         """
      ).on('userId -> userId).as(Notification.parser.*)
    }
  }

  def markAsRead(id: Long): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           UPDATE Notification
           SET unread = false
           WHERE id = {id}
         """
      ).on('id -> id).executeUpdate()
    }
  }

  def delete(id: Long): Future[Int] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           DELETE FROM Notification
           WHERE id = {id}
         """
      ).on('id -> id).executeUpdate()
    }
  }

  def create(notification: Notification): Future[Option[Long]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO Notification (userId, fromUser, notificationType, subject, body, unread)
           VALUES ({userId}, {fromUser}, {notificationType}, {subject}, {body}, {unread})
         """
      ).on(
        'userId -> notification.userId,
        'fromUser -> notification.fromUser.map(_.toString).orNull,
        'notificationType -> notification.notificationType.toString,
        'subject -> notification.subject,
        'body -> notification.body,
        'unread -> notification.unread
      ).executeInsert()
    }
  }

}
