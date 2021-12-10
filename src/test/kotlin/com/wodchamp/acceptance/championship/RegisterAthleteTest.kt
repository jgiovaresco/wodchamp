package com.wodchamp.acceptance.championship

import com.wodchamp.domain.Athlete
import com.wodchamp.domain.Division
import com.wodchamp.domain.Gender
import com.wodchamp.domain.Level
import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.domain.championship.RegisterAthleteResult
import com.wodchamp.domain.error.ErrorCode
import com.wodchamp.fixtures.ChampionshipBuilder.Builder.aChampionship
import com.wodchamp.fixtures.aFemaleAthlete
import com.wodchamp.fixtures.aMaleAthlete
import com.wodchamp.fixtures.aRegisterAthleteCommand
import com.wodchamp.fixtures.anAthleteRegisteredEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class RegisterAthleteTest {

  private lateinit var championship: Championship
  private var rxMaleDivision = Division(Gender.Male, Level.RX)
  private var scaledMaleDivision = Division(Gender.Male, Level.SCALED)
  private var rxFemaleDivision = Division(Gender.Female, Level.RX)
  private var scaledFemaleDivision = Division(Gender.Female, Level.SCALED)

  @BeforeEach
  internal fun setUp() {
    championship =
      aChampionship(divisions = listOf(rxFemaleDivision, rxMaleDivision, scaledMaleDivision)).get()
  }

  @Test
  fun `should raise AthleteRegistered event`() {
    val command = aRegisterAthleteCommand(division = rxMaleDivision)

    val result = championship.registerAthlete(command)

    expectThat(result).isA<RegisterAthleteResult.Success>().and {
      get { events }.hasSize(1).and {
        first().isA<ChampionshipEvent.AthleteRegistered>().and {
          get { athlete }.isEqualTo(command.athlete)
          get { division }.isEqualTo(command.division)
        }
      }
    }
  }

  @Test
  fun `should throw exception when registering athlete on a not created championship`() {
    val championship = Championship()
    val command = aRegisterAthleteCommand(division = rxMaleDivision)

    expectCatching { championship.registerAthlete(command) }
      .isFailure()
      .isA<IllegalStateException>()
      .and { get { message }.isEqualTo("Championship must be created") }
  }

  @Test
  fun `should prevent from registering an athlete in unsupported division`() {
    val command =
      aRegisterAthleteCommand(athlete = aFemaleAthlete(), division = scaledFemaleDivision)

    val result = championship.registerAthlete(command)

    expectThat(result).isA<RegisterAthleteResult.UnavailableDivision>()
  }

  @Test
  fun `should prevent from registering an athlete in the wrong division`() {
    val command = aRegisterAthleteCommand(division = rxFemaleDivision)

    val result = championship.registerAthlete(command)

    expectThat(result).isA<RegisterAthleteResult.IncorrectDivision>().and {
      get { code }.isEqualTo(ErrorCode.INC_DIVISION_GENDER)
    }
  }

  @Test
  fun `should register the first athlete in the provided division`() {
    val athlete1 = aMaleAthlete()
    val division = rxMaleDivision

    val events = listOf(anAthleteRegisteredEvent(id = championship.id!!, athlete = athlete1, division))

    val championship = championship.applyAll(events)

    expectThat(championship.registeredAthletes) {
      get { get(division)!!.map(Athlete::name) }.containsExactly(athlete1.name)
    }
  }
}
