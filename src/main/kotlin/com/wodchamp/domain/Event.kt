package com.wodchamp.domain

data class Event(
  val id: String,
  val name: String,
  val description: String,
  val scoreType: ScoreType,
)
