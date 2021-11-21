package com.wodchamp.utils

import java.util.UUID

class IdGenerator {
  companion object Uuid {
    var generateFn: () -> String = { UUID.randomUUID().toString() }

    fun generate(): String {
      return generateFn()
    }

    fun overrideGenerateFn(fn: () -> String) {
      IdGenerator.generateFn = fn
    }
  }
}
