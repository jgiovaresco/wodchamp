package com.wodchamp.domain.championship

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

  override fun getDomainEntityId(): String {
    return id
  }
}
