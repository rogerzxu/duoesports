package com.rxu.duoesports.util

case class GetUserException(msg: String) extends Exception(msg)
case class SavePasswordException(msg: String) extends Exception(msg)
case class ActivateUserException(msg: String) extends Exception(msg)
case class CreateUserException(msg: String) extends Exception(msg)
case class UpdateUserException(msg: String) extends Exception(msg)
case class GenerateAuthTokenException(msg: String) extends Exception(msg)
