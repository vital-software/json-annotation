package com.github.vitalsoftware.macros

import scala.language.experimental.macros
import scala.reflect.macros._

/**
 * Context has been deprecated in Scala 2.11, blackbox.Context is used instead
 */
object CrossVersionDefs {
  type CrossVersionContext = blackbox.Context
}
