package com.wodchamp.fixtures

import com.wodchamp.domain.Division
import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent

class ChampionshipBuilder(divisions: List<Division>) {

  var championship: Championship

  init {
    championship = Championship().applyAll(aChampionshipCreatedEvent(divisions = divisions))
  }

  companion object Builder {
    fun aChampionship(
      divisions: List<Division> = listOf(maleRXDivision(), femaleRXDivision())
    ): ChampionshipBuilder {
      return ChampionshipBuilder(divisions)
    }
  }

  fun withAthletes(
    athletes: List<ChampionshipEvent.AthleteRegistered> =
      listOf(
        anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
        anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
        anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
        anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
      )
  ): ChampionshipBuilder {
    championship = championship.applyAll(athletes)
    return this
  }

  fun withEvents(
    events: List<ChampionshipEvent.EventRegistered> =
      listOf(
        anEventRegisteredEvent(name = faker.crossfit.heroWorkouts()),
        anEventRegisteredEvent(name = faker.crossfit.girlWorkouts()),
        anEventRegisteredEvent(name = faker.crossfit.heroWorkouts()),
      )
  ): ChampionshipBuilder {
    championship = championship.applyAll(events)
    return this
  }

  fun get(): Championship {
    return championship
  }
}
