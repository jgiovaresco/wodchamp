package com.wodchamp.domain.championship

import com.google.common.base.Strings.isNullOrEmpty
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.wodchamp.domain.Athlete
import com.wodchamp.domain.Division
import com.wodchamp.domain.DivisionAcceptResult
import com.wodchamp.domain.error.ErrorCode
import com.wodchamp.framework.DomainAggregate
import com.wodchamp.framework.DomainEvent
import com.wodchamp.utils.IdGenerator
import com.wodchamp.utils.TimeProvider
import java.time.LocalDate

open class Championship : DomainAggregate<String, ChampionshipEvent>() {
  var status: ChampionshipStatus = ChampionshipStatus.Initial
  lateinit var info: ChampionshipInfo
  var registeredAthletes: ListMultimap<Division, Athlete> = ArrayListMultimap.create()

  fun createChampionship(
    command: ChampionshipCommand.CreateChampionship
  ): CreateChampionshipResult {
    check(!isNullOrEmpty(command.name)) { "Championship name must not be empty" }
    check(command.date.isAfter(TimeProvider.today())) {
      "Championship date must schedule in the future"
    }

    val id = IdGenerator.generate()
    return CreateChampionshipResult.Success(
      listOf(
        ChampionshipEvent.ChampionshipCreated(
          id,
          name = command.name,
          date = command.date,
          divisions = command.divisions,
        )
      )
    )
  }

  fun registerAthlete(command: ChampionshipCommand.RegisterAthlete): RegisterAthleteResult {
    check(status == ChampionshipStatus.Created) { "Championship must be created" }

    val division =
      info.divisions.find { it == command.division }
        ?: return RegisterAthleteResult.UnavailableDivision

    when (division.accept(command.athlete)) {
      DivisionAcceptResult.Success -> {}
      DivisionAcceptResult.IncorrectGender ->
        return RegisterAthleteResult.IncorrectDivision(ErrorCode.INC_DIVISION_GENDER)
    }

    return RegisterAthleteResult.Success(
      listOf(
        ChampionshipEvent.AthleteRegistered(
          id = id!!,
          athlete = command.athlete,
          division = command.division,
        )
      )
    )
  }

  override fun applyAll(event: List<ChampionshipEvent>): Championship {
    return event.fold(this) { championship, nextEvent ->
      when (nextEvent) {
        is ChampionshipEvent.ChampionshipCreated -> championship.apply(nextEvent)
        is ChampionshipEvent.AthleteRegistered -> championship.apply(nextEvent)
      }
    }
  }

  private fun apply(event: ChampionshipEvent.ChampionshipCreated): Championship {
    id = event.id
    info =
      ChampionshipInfo(
        name = event.name,
        date = event.date,
        divisions = event.divisions,
      )
    status = ChampionshipStatus.Created

    return this
  }

  private fun apply(event: ChampionshipEvent.AthleteRegistered): Championship {

    registeredAthletes.put(event.division, event.athlete)

    return this
  }
}

enum class ChampionshipStatus {
  Initial,
  Created
}

data class ChampionshipInfo(val name: String, val date: LocalDate, val divisions: List<Division>)

sealed class CreateChampionshipResult {
  class Success(val events: List<DomainEvent>) : CreateChampionshipResult()
}

sealed class RegisterAthleteResult {
  class Success(val events: List<DomainEvent>) : RegisterAthleteResult()
  object UnavailableDivision : RegisterAthleteResult()
  class IncorrectDivision(val code: ErrorCode) : RegisterAthleteResult()
}
