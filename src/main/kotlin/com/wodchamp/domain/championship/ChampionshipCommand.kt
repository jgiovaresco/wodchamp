package com.wodchamp.domain.championship

import com.wodchamp.domain.Athlete
import com.wodchamp.domain.Division
import com.wodchamp.domain.Score
import com.wodchamp.domain.ScoreType
import com.wodchamp.framework.Command
import java.time.LocalDate

sealed class ChampionshipCommand : Command {
  data class CreateChampionship(
    val name: String,
    val date: LocalDate,
    val divisions: List<Division>
  ) : ChampionshipCommand()

  data class RegisterAthlete(val athlete: Athlete, val division: Division) : ChampionshipCommand()

  data class RegisterEvent(val name: String, val description: String, val scoreType: ScoreType) :
    ChampionshipCommand()

  data class RegisterScore(val athleteId: String, val score: Score) : ChampionshipCommand()
}
