package com.wodchamp.acceptance.championship

import com.wodchamp.domain.Event
import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.domain.championship.RegisterEventResult
import com.wodchamp.fixtures.ChampionshipBuilder.Builder.aChampionship
import com.wodchamp.fixtures.aRegisterEventCommand
import com.wodchamp.fixtures.anEventRegisteredEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class RegisterEventTest {
  private lateinit var championship: Championship

  @BeforeEach
  internal fun setUp() {
    championship = aChampionship().get()
  }

  @Test
  fun `should raise EventRegistered event`() {
    val command = aRegisterEventCommand()

    val result = championship.registerEvent(command)

    expectThat(result).isA<RegisterEventResult.Success>().and {
      get { events }.hasSize(1).and {
        first().isA<ChampionshipEvent.EventRegistered>().and {
          get { eventId }.isNotEmpty()
          get { name }.isEqualTo(command.name)
          get { description }.isEqualTo(command.description)
        }
      }
    }
  }

  @Test
  fun `should throw exception when registering event on a not created championship`() {
    val championship = Championship()
    val command = aRegisterEventCommand()

    expectCatching { championship.registerEvent(command) }
      .isFailure()
      .isA<IllegalStateException>()
      .and { get { message }.isEqualTo("Championship must be created") }
  }

  @Test
  fun `should register the first event`() {
    val events = listOf(anEventRegisteredEvent(id = championship.id!!))

    val championship = championship.applyAll(events)

    expectThat(championship.registeredEvents) {
      map(Event::id).containsExactly(events[0].eventId)
      map(Event::name).containsExactly(events[0].name)
      map(Event::description).containsExactly(events[0].description)
    }
  }

  @Test
  fun `should register a second event`() {
    val event1 = anEventRegisteredEvent(id = championship.id!!)
    val event2 = anEventRegisteredEvent(id = championship.id!!)
    val events = listOf(event1, event2)

    val championship = championship.applyAll(events)

    expectThat(championship.registeredEvents) {
      map(Event::id).containsExactly(event1.eventId, event2.eventId)
      map(Event::name).containsExactly(event1.name, event2.name)
      map(Event::description).containsExactly(event1.description, event2.description)
    }
  }
}
