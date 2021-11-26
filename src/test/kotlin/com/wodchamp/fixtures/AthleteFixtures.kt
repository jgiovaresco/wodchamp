package com.wodchamp.fixtures

import com.wodchamp.domain.Athlete
import com.wodchamp.domain.Gender

fun aMaleAthlete(
  name: String = faker.crossfit.maleAthletes(),
): Athlete {
  return Athlete(name, Gender.Male)
}

fun aFemaleAthlete(
  name: String = faker.crossfit.femaleAthletes(),
): Athlete {
  return Athlete(name, Gender.Female)
}
