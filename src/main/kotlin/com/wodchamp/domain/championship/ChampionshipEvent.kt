package com.wodchamp.domain.championship

import com.wodchamp.domain.Athlete
import com.wodchamp.domain.Division
import com.wodchamp.framework.DomainEvent
import java.time.LocalDate

sealed class ChampionshipEvent(open val id: String) : DomainEvent {
  class ChampionshipCreated(
    override val id: String,
    val name: String,
    val date: LocalDate,
    val divisions: List<Division>,
  ) : ChampionshipEvent(id)

  data class AthleteRegistered(
    override val id: String,
    val athlete: Athlete,
    val division: Division
  ) : ChampionshipEvent(id)

  data class EventRegistered(
    override val id: String,
    val eventId: String,
    val name: String,
    val description: String
  ) : ChampionshipEvent(id)

  data class ChampionshipStarted(override val id: String) : ChampionshipEvent(id)

  override fun getDomainEntityId(): String {
    return id
  }
}
