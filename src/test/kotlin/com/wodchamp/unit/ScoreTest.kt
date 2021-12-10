package com.wodchamp.unit

import com.wodchamp.domain.RepScore
import com.wodchamp.domain.TimeScore
import com.wodchamp.domain.WeightScore
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThan

internal class ScoreTest {
  @Test
  internal fun `a faster time rank better`() {
    val faster = TimeScore(10u)
    val slower = TimeScore(20u)

    expectThat(faster).isGreaterThan(slower)
  }

  @Test
  internal fun `same time is equal`() {
    val time1 = TimeScore(10u)
    val time2 = TimeScore(10u)

    expectThat(time1).isEqualTo(time2)
  }

  @Test
  internal fun `a heavier result rank better`() {
    val heavier = WeightScore(120u)
    val lighter = WeightScore(80u)

    expectThat(heavier).isGreaterThan(lighter)
  }

  @Test
  internal fun `same weight is equal`() {
    val weight1 = WeightScore(120u)
    val weight2 = WeightScore(120u)

    expectThat(weight1).isEqualTo(weight2)
  }

  @Test
  internal fun `a more rep result rank better`() {
    val moreReps = RepScore(150u)
    val lessReps = RepScore(95u)

    expectThat(moreReps).isGreaterThan(lessReps)
  }

  @Test
  internal fun `same reps is equal`() {
    val reps1 = RepScore(120u)
    val reps2 = RepScore(120u)

    expectThat(reps1).isEqualTo(reps2)
  }
}
