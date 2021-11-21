package com.wodchamp.framework

import com.wodchamp.domain.championship.Championship
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class DomainAggregate<TId : Any, in TEvent>(open var id: TId? = null) {
  private val log: Logger = LoggerFactory.getLogger(this.javaClass)

  fun applyAll(vararg events: TEvent): Championship {
    return applyAll(events.toList())
  }

  abstract fun applyAll(event: List<TEvent>): Championship
}
