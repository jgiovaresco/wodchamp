package com.wodchamp.fixtures

import com.wodchamp.domain.Division
import com.wodchamp.domain.Gender
import com.wodchamp.domain.Level
import com.wodchamp.domain.championship.Championship

class ChampionshipBuilder(divisions: List<Division>) {

  var championship: Championship

  init {
    championship = Championship().applyAll(aChampionshipCreatedEvent(divisions = divisions))
  }

  companion object Builder {
    fun aChampionship(
      divisions: List<Division> =
        listOf(Division(Gender.Male, Level.RX), Division(Gender.Female, Level.RX))
    ): ChampionshipBuilder {
      return ChampionshipBuilder(divisions)
    }
  }

  fun get(): Championship {
    return championship
  }
}
