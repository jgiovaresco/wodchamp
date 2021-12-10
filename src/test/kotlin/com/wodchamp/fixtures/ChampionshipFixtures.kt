package com.wodchamp.fixtures

import com.wodchamp.domain.*
import com.wodchamp.domain.championship.ChampionshipCommand
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.utils.TimeProvider
import java.time.LocalDate

fun aCreateChampionshipCommand(
  name: String = faker.crossfit.competitions(),
  date: LocalDate = TimeProvider.today().plusDays(5),
  divisions: List<Division> =
    listOf(Division(Gender.Male, Level.RX), Division(Gender.Female, Level.RX))
): ChampionshipCommand.CreateChampionship {
  return ChampionshipCommand.CreateChampionship(name, date, divisions)
}

fun aChampionshipCreatedEvent(
  id: String = faker.random.nextUUID(),
  name: String = faker.crossfit.competitions(),
  date: LocalDate = TimeProvider.today().plusDays(5),
  divisions: List<Division> =
    listOf(Division(Gender.Male, Level.RX), Division(Gender.Female, Level.RX))
): ChampionshipEvent.ChampionshipCreated {
  return ChampionshipEvent.ChampionshipCreated(id, name, date, divisions)
}

fun aRegisterAthleteCommand(
  athlete: Athlete = aMaleAthlete(),
  division: Division
): ChampionshipCommand.RegisterAthlete {
  return ChampionshipCommand.RegisterAthlete(athlete, division)
}

fun anAthleteRegisteredEvent(
  id: String = faker.random.nextUUID(),
  athlete: Athlete = aMaleAthlete(),
  division: Division = Division(Gender.Male, Level.RX)
): ChampionshipEvent.AthleteRegistered {
  return ChampionshipEvent.AthleteRegistered(id, athlete, division)
}

fun aRegisterEventCommand(
  name: String = faker.crossfit.heroWorkouts(),
  description: String = faker.bigBangTheory.quotes(),
  scoreType: ScoreType = faker.random.nextEnum(),
): ChampionshipCommand.RegisterEvent {
  return ChampionshipCommand.RegisterEvent(name, description, scoreType)
}

fun anEventRegisteredEvent(
  id: String = faker.random.nextUUID(),
  eventId: String = faker.random.nextUUID(),
  name: String = faker.name.femaleFirstName(),
  description: String = faker.bigBangTheory.quotes(),
  scoreType: ScoreType = faker.random.nextEnum(),
): ChampionshipEvent.EventRegistered {
  return ChampionshipEvent.EventRegistered(id, eventId, name, description, scoreType)
}

fun aChampionshipStartedEvent(
  id: String = faker.random.nextUUID()
): ChampionshipEvent.ChampionshipStarted {
  return ChampionshipEvent.ChampionshipStarted(id)
}

fun aRegisterScoreCommand(
  athleteId: String = faker.random.nextUUID(),
  score: Score = aWeightScore(),
): ChampionshipCommand.RegisterScore {
  return ChampionshipCommand.RegisterScore(athleteId, score)
}
