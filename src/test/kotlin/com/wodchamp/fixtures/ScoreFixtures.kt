package com.wodchamp.fixtures

import com.wodchamp.domain.RepScore
import com.wodchamp.domain.TimeScore
import com.wodchamp.domain.WeightScore

fun aTimeScore(value: UInt = faker.random.nextInt(180, 420).toUInt()): TimeScore {
  return TimeScore(value)
}

fun aRepScore(value: UInt = faker.random.nextInt(50, 250).toUInt()): RepScore {
  return RepScore(value)
}

fun aWeightScore(value: UInt = faker.random.nextInt(40, 110).toUInt()): WeightScore {
  return WeightScore(value)
}
