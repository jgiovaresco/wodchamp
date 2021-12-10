package com.wodchamp.acceptance.championship

import com.wodchamp.domain.Athlete
import com.wodchamp.domain.ScoreType
import com.wodchamp.domain.championship.Championship
import com.wodchamp.domain.championship.ChampionshipEvent
import com.wodchamp.domain.championship.RegisterScoreResult
import com.wodchamp.fixtures.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class RegisterScoreTest {
  private lateinit var championship: Championship
  private lateinit var anAthlete: Athlete

  @BeforeEach
  fun setUp() {
    anAthlete = aMaleAthlete()

    championship =
      ChampionshipBuilder.aChampionship()
        .withEvents(
          anEventRegisteredEvent(scoreType = ScoreType.Time),
          anEventRegisteredEvent(scoreType = ScoreType.Reps),
          anEventRegisteredEvent(scoreType = ScoreType.Weight),
        )
        .withAthletes(
          anAthleteRegisteredEvent(athlete = anAthlete, division = maleRXDivision()),
          anAthleteRegisteredEvent(athlete = aMaleAthlete(), division = maleRXDivision()),
          anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
          anAthleteRegisteredEvent(athlete = aFemaleAthlete(), division = femaleRXDivision()),
        )
        .started()
        .get()
  }

  @Test
  fun `should raise a EventScoreRegistered event`() {
    val command = aRegisterScoreCommand(anAthlete.id, aTimeScore())

    val result = championship.registerScore(command)

    expectThat(result).isA<RegisterScoreResult.Success>().and {
      get { events }.hasSize(1).and {
        first().isA<ChampionshipEvent.EventScoreRegistered>().and {
          get { id }.isEqualTo(championship.id)
          get { athleteId }.isEqualTo(anAthlete.id)
        }
      }
    }
  }

  @Test
  fun `should throw exception when registering a not started championship`() {
    val championship = Championship()

    expectCatching { championship.registerScore(aRegisterScoreCommand()) }
      .isFailure()
      .isA<IllegalStateException>()
      .and { get { message }.isEqualTo("Championship must be started") }
  }

  @Test
  fun `should prevent from registering score of an unknown athlete`() {
    val command = aRegisterScoreCommand()
    val result = championship.registerScore(command)

    expectThat(result).isA<RegisterScoreResult.UnknownAthlete>().and {
      get { athleteId }.isEqualTo(command.athleteId)
    }
  }

  @Test
  fun `should prevent from registering an incompatible score`() {
    val command = aRegisterScoreCommand(anAthlete.id, aWeightScore())
    val result = championship.registerScore(command)

    expectThat(result).isA<RegisterScoreResult.IncompatibleScore>().and {
      get { score }.isEqualTo(command.score)
    }
  }
}
