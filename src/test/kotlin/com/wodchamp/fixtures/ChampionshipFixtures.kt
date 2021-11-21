package com.wodchamp.fixtures

import com.wodchamp.domain.Division
import com.wodchamp.domain.Gender
import com.wodchamp.domain.Level
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
