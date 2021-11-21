package com.wodchamp.domain.championship

import com.wodchamp.domain.Division
import com.wodchamp.framework.Command
import java.time.LocalDate

sealed class ChampionshipCommand : Command {
  data class CreateChampionship(
    val name: String,
    val date: LocalDate,
    val divisions: List<Division>
  ) : ChampionshipCommand()
}
