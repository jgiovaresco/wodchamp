package com.wodchamp.domain.championship

import com.google.common.base.Strings.isNullOrEmpty
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.wodchamp.domain.*
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
  var registeredEvents: MutableList<Event> = mutableListOf()
  lateinit var currentEvent: Event

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

  fun registerEvent(command: ChampionshipCommand.RegisterEvent): RegisterEventResult {
    check(status == ChampionshipStatus.Created) { "Championship must be created" }

    val eventId = IdGenerator.generate()

    return RegisterEventResult.Success(
      listOf(
        ChampionshipEvent.EventRegistered(
          id = id!!,
          eventId,
          name = command.name,
          description = command.description,
          scoreType = command.scoreType
        )
      )
    )
  }

  fun start(): StartResult {
    check(status == ChampionshipStatus.Created) { "Championship must be created" }
    check(registeredEvents.size > 0) { "At least one event must be registered" }

    val allDivisionsHaveEnoughAthlete =
      info.divisions.all { (registeredAthletes.asMap().getOrDefault(it, emptyList()).size >= 2) }

    if (allDivisionsHaveEnoughAthlete) {
      return StartResult.Success(events = listOf(ChampionshipEvent.ChampionshipStarted(id!!)))
    }

    return StartResult.NotEnoughAthlete
  }

  fun registerScore(command: ChampionshipCommand.RegisterScore): RegisterScoreResult {
    check(status == ChampionshipStatus.Started) { "Championship must be started" }

    val athleteExists =
      registeredAthletes.asMap().entries.any {
        it.value.any { athlete -> athlete.id == command.athleteId }
      }
    if (!athleteExists) {
      return RegisterScoreResult.UnknownAthlete(command.athleteId)
    }

    if (currentEvent.scoreType != command.score.type) {
      return RegisterScoreResult.IncompatibleScore(command.score)
    }

    return RegisterScoreResult.Success(
      events =
        listOf(ChampionshipEvent.EventScoreRegistered(id!!, command.athleteId, command.score))
    )
  }

  override fun applyAll(event: List<ChampionshipEvent>): Championship {
    return event.fold(this) { championship, nextEvent ->
      when (nextEvent) {
        is ChampionshipEvent.ChampionshipCreated -> championship.apply(nextEvent)
        is ChampionshipEvent.AthleteRegistered -> championship.apply(nextEvent)
        is ChampionshipEvent.EventRegistered -> championship.apply(nextEvent)
        is ChampionshipEvent.ChampionshipStarted -> championship.apply(nextEvent)
        is ChampionshipEvent.EventScoreRegistered -> championship.apply(nextEvent)
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

  private fun apply(event: ChampionshipEvent.EventRegistered): Championship {
    registeredEvents.add(Event(event.eventId, event.name, event.description, event.scoreType))
    return this
  }

  private fun apply(event: ChampionshipEvent.ChampionshipStarted): Championship {
    status = ChampionshipStatus.Started
    currentEvent = registeredEvents.first()
    return this
  }

  private fun apply(event: ChampionshipEvent.EventScoreRegistered): Championship {
    return this
  }
}

enum class ChampionshipStatus {
  Initial,
  Created,
  Started
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

sealed class RegisterEventResult {
  class Success(val events: List<DomainEvent>) : RegisterEventResult()
}

sealed class StartResult {
  class Success(val events: List<DomainEvent>) : StartResult()
  object NotEnoughAthlete : StartResult()
}

sealed class RegisterScoreResult {
  class Success(val events: List<DomainEvent>) : RegisterScoreResult()

  class UnknownAthlete(val athleteId: String) : RegisterScoreResult()
  class IncompatibleScore(val score: Score) : RegisterScoreResult()
}
