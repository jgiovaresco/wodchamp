package com.wodchamp.acceptance.championship

import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.domain.championship.ChampionshipStatus
import com.wodchamp.domain.championship.CreateChampionshipResult
import com.wodchamp.fixtures.aChampionshipCreatedEvent
import com.wodchamp.fixtures.aCreateChampionshipCommand
import com.wodchamp.utils.TimeProvider
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

internal class CreateChampionshipTest {

  @Test
  fun `should raise a ChampionshipCreated event`() {
    val command = aCreateChampionshipCommand()
    val championship = Championship()

    val result = championship.createChampionship(command)

    expectThat(result).isA<CreateChampionshipResult.Success>().and {
      get { events }.hasSize(1).and {
        first().isA<ChampionshipEvent.ChampionshipCreated>().and {
          get { name }.isEqualTo(command.name)
          get { date }.isEqualTo(command.date)
          get { divisions }.isEqualTo(command.divisions)
        }
      }
    }
  }

  @Test
  fun `should apply ChampionshipCreated event`() {
    val event = aChampionshipCreatedEvent()
    val championship = Championship().applyAll(event)

    expectThat(championship) {
      get { id }.isEqualTo(event.id)
      get { status }.isEqualTo(ChampionshipStatus.Created)
      get { info.name }.isEqualTo(event.name)
      get { info.date }.isEqualTo(event.date)
      get { info.divisions }.isEqualTo(event.divisions)
    }
  }

  @Test
  fun `should throw exception when creating a championship with empty name`() {
    val championship = Championship()
    val command = aCreateChampionshipCommand(name = "")

    expectCatching { championship.createChampionship(command) }
      .isFailure()
      .isA<IllegalStateException>()
      .and { get { message }.isEqualTo("Championship name must not be empty") }
  }

  @Test
  fun `should throw exception when creating a championship scheduled in the past`() {
    val championship = Championship()
    val dateInPast = TimeProvider.today().minusDays(2)
    val command = aCreateChampionshipCommand(date = dateInPast)

    expectCatching { championship.createChampionship(command) }
      .isFailure()
      .isA<IllegalStateException>()
      .and { get { message }.isEqualTo("Championship date must schedule in the future") }
  }
}
