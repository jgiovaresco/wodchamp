package com.wodchamp.domain.championship

import com.google.common.base.Strings.isNullOrEmpty
import com.wodchamp.domain.Division
import com.wodchamp.framework.DomainAggregate
import com.wodchamp.framework.DomainEvent
import com.wodchamp.utils.IdGenerator
import com.wodchamp.utils.TimeProvider
import java.time.LocalDate

open class Championship : DomainAggregate<String, ChampionshipEvent>() {
  var status: ChampionshipStatus = ChampionshipStatus.Initial
  lateinit var info: ChampionshipInfo

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

  override fun applyAll(event: List<ChampionshipEvent>): Championship {
    return event.fold(this) { championship, nextEvent ->
      when (nextEvent) {
        is ChampionshipEvent.ChampionshipCreated -> championship.apply(nextEvent)
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
}

enum class ChampionshipStatus {
  Initial,
  Created
}

data class ChampionshipInfo(val name: String, val date: LocalDate, val divisions: List<Division>)

sealed class CreateChampionshipResult {
  class Success(val events: List<DomainEvent>) : CreateChampionshipResult()
}
