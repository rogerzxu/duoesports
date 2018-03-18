package com.rxu.duoesports.util

case class ActivateUserException(msg: String) extends Exception(msg)
case class CreateUserException(msg: String) extends Exception(msg)
case class GetUserException(msg: String) extends Exception(msg)
case class UpdateUserException(msg: String) extends Exception(msg)

case class CreateTeamException(msg: String) extends Exception(msg)
case class DuplicateTeamException(msg: String) extends Exception(msg)
case class GetTeamException(msg: String) extends Exception(msg)
case class UpdateTeamException(msg: String) extends Exception(msg)
case class DeleteTeamException(msg: String) extends Exception(msg)

case class AddSummonerException(msg: String) extends Exception(msg)

case class GenerateAuthTokenException(msg: String) extends Exception(msg)

case class RiotApiException(msg: String) extends Exception(msg)

case class SavePasswordException(msg: String) extends Exception(msg)
