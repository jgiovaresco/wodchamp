package com.wodchamp.domain

data class Division(val gender: Gender, val level: Level) {
  fun accept(athlete: Athlete): DivisionAcceptResult {
    if (gender != athlete.gender) {
      return DivisionAcceptResult.IncorrectGender
    }
    return DivisionAcceptResult.Success
  }
}

sealed class DivisionAcceptResult {
  object Success : DivisionAcceptResult()

  object IncorrectGender : DivisionAcceptResult()
}
