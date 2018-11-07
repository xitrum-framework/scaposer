package com.github.fbaierl.scaposer

import scala.util.parsing.input.Position

// Not an [[Exception]] because its creation is slow.
case class ParseFailure(msg: String, position: Position) {
  /** Returns a visual representation of failure position. */
  override def toString = {
    s"$msg:\n${position.longString}"
  }
}
