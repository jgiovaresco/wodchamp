package com.wodchamp.framework

interface DomainEvent : Message {
  fun getDomainEntityId(): String
}
