package com.wodchamp.fixtures

import com.wodchamp.domain.Division
import com.wodchamp.domain.Gender
import com.wodchamp.domain.Level

fun maleRXDivision(): Division {
  return Division(Gender.Male, Level.RX)
}

fun femaleRXDivision(): Division {
  return Division(Gender.Female, Level.RX)
}

fun maleScaledDivision(): Division {
  return Division(Gender.Male, Level.SCALED)
}

fun femaleScaledDivision(): Division {
  return Division(Gender.Female, Level.SCALED)
}
