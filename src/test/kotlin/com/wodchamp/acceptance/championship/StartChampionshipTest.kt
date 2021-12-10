package com.wodchamp.acceptance.championship

import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.domain.championship.ChampionshipStatus
import com.wodchamp.domain.championship.StartResult
import com.wodchamp.fixtures.*
import com.wodchamp.fixtures.ChampionshipBuilder.Builder.aChampionship
import java.util.stream.Stream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class StartChampionshipTest {
  private lateinit var championship: Championship

  @BeforeEach
  fun setUp() {
    championship = aChampionship().withEvents().withAthletes().get()
  }

  @Test
  fun `should raise a ChampionshipStarted event`() {
    val result = championship.start()

    expectThat(result).isA<StartResult.Success>().and {
      get { events }.hasSize(1).and {
        first().isA<ChampionshipEvent.ChampionshipStarted>().and {
          get { id }.isEqualTo(championship.id)
        }
      }
    }
  }

  @Test
  fun `should throw exception when starting a not created championship`() {
    val championship = Championship()

    expectCatching { championship.start() }.isFailure().isA<IllegalStateException>().and {
      get { message }.isEqualTo("Championship must be created")
    }
  }

  @Test
  fun `should throw exception when starting a championship without event registered`() {
    championship = aChampionship().withAthletes().get()

    expectCatching { championship.start() }.isFailure().isA<IllegalStateException>().and {
      get { message }.isEqualTo("At least one event must be registered")
    }
  }

  @ParameterizedTest
  @MethodSource(
    "noAthleteRegistered",
    "oneAthleteRegisteredEvent",
    "notEnoughMaleAthlete",
    "notEnoughFemaleAthlete"
  )
  fun `should prevent starting a championship with not enough athlete`(
    athletes: List<ChampionshipEvent.AthleteRegistered>
  ) {
    championship = aChampionship().withEvents().withAthletes(athletes).get()

    val result = championship.start()

    expectThat(result).isA<StartResult.NotEnoughAthlete>()
  }

  @Test
  internal fun `should start the championship`() {
    championship = championship.applyAll(aChampionshipStartedEvent(championship.id!!))

    expectThat(championship) {
      get { status }.isEqualTo(ChampionshipStatus.Started)
      get { currentEvent }.isEqualTo(championship.registeredEvents.first())
    }
  }

  companion object {
    @JvmStatic
    fun noAthleteRegistered(): Stream<List<ChampionshipEvent.AthleteRegistered>> {
      return Stream.of(emptyList())
    }

    @JvmStatic
    fun oneAthleteRegisteredEvent(): Stream<List<ChampionshipEvent.AthleteRegistered>> {
      return Stream.of(
        listOf(
          anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
          anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
        )
      )
    }

    @JvmStatic
    fun notEnoughMaleAthlete(): Stream<List<ChampionshipEvent.AthleteRegistered>> {
      return Stream.of(
        listOf(
          anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
          anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
        )
      )
    }

    @JvmStatic
    fun notEnoughFemaleAthlete(): Stream<List<ChampionshipEvent.AthleteRegistered>> {
      return Stream.of(
        listOf(
          anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
          anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
        )
      )
    }
  }
}
