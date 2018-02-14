package com.rxu.duoesports.service

import com.google.inject.Inject
import com.rxu.duoesports.dto.AddSummonerForm
import com.rxu.duoesports.models.{Rank, User, UserAlt, VerificationCode}
import com.rxu.duoesports.riot.RiotClient
import com.rxu.duoesports.riot.dto.RiotSummonerLeague
import com.rxu.duoesports.service.dao.VerificationCodeDao
import com.rxu.duoesports.util.AddSummonerException
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class VerificationService @Inject()(
  verificationCodeDao: VerificationCodeDao,
  userService: UserService,
  userAltService: UserAltService,
  riotClient: RiotClient
)(
  implicit ec: ExecutionContext
) extends LazyLogging {

  def generateVerificationCode(user: User): Future[VerificationCode] = {
    logger.debug(s"Generating verification code for $user")
    val verificationCode = VerificationCode(
      userId = user.id,
      code = UUID.randomUUID.toString
    )
    verificationCodeDao.upsert(verificationCode) map {_ => verificationCode}
  }

  def addSummoner(user: User, addSummonerForm: AddSummonerForm): Future[Unit] = {
    logger.info(s"Adding summoner for user ${user.id}: $addSummonerForm")
    for {
      maybeRiotSummoner <- riotClient.findBySummonerName(addSummonerForm.summonerName, addSummonerForm.getRegion)
      riotSummoner <- maybeRiotSummoner match {
        case Some(riotSummoner) => Future.successful(riotSummoner)
        case None => Future.failed(AddSummonerException(s"Could not find summoner named ${addSummonerForm.summonerName} from RIOT"))
      }
      maybeExistingSummoner <- userService.findBySummonerNameOrId(riotSummoner.name, riotSummoner.id, addSummonerForm.getRegion)
      _ <- maybeExistingSummoner match {
        case existing if existing.nonEmpty => Future.failed(AddSummonerException(s"${addSummonerForm.summonerName} is already linked to a different user"))
        case _ => Future.successful(())
      }
      maybeExistingAlt <- userAltService.findBySummonerNameOrId(riotSummoner.name, riotSummoner.id, addSummonerForm.getRegion)
      _ <- maybeExistingAlt match {
        case existing if existing.nonEmpty => Future.failed(AddSummonerException(s"${addSummonerForm.summonerName} is already linked to a different user"))
        case _ => Future.successful(())
      }
      maybeVerificationCode <- verificationCodeDao.findByUserId(user.id)
      verificationCode <- maybeVerificationCode match {
        case Some(verificationCode) => Future.successful(verificationCode)
        case _ => Future.failed(AddSummonerException(s"There was a problem with the generated verification code"))
      }
      riotCode <- riotClient.getVerificationCode(riotSummoner.id, addSummonerForm.getRegion)
      _ <- if(riotCode equals verificationCode.code) {
        Future.successful(())
      } else Future.failed(AddSummonerException(s"The verification code provided did not match the one stored on the LoL account. "
        + "Please try again. If the problem persists, restart your LoL Client and re-enter the verification code."))
      user <- userService.getById(user.id)
      _ <- if (user.verified) {
        userAltService.create(UserAlt(user.id, riotSummoner.name, riotSummoner.id, addSummonerForm.getRegion))
      } else {
        userService.putSummoner(user, riotSummoner.name, riotSummoner.id, addSummonerForm.getRegion)
      }
      riotSummonerLeagues <- riotClient.getLeagueForSummoner(riotSummoner.id, addSummonerForm.getRegion)
      _ <- updateRankIfNecessary(user, riotSummonerLeagues)
    } yield {
      logger.info(s"Successfully verified $addSummonerForm for ${user.id}")
    }
  }

  private def updateRankIfNecessary(user: User, userLeagues: Seq[RiotSummonerLeague]): Future[Unit] = {
    val maybeRiotRank = Rank.max(userLeagues.filter(_.isSoloQOrFlex).map(_.getRank))
    (user.rank, maybeRiotRank) match {
      case (Some(userRank), Some(riotRank)) =>
        if(Rank.compare(riotRank, userRank) > 0) userService.update(user, riotRank)
        else Future.successful(())
      case (None, Some(riotRank)) => userService.update(user, riotRank)
      case _ => Future.successful(())
    }
  }

}
