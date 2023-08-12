package com.compiler.server

import io.micronaut.runtime.Micronaut.run

class CompilerApplication

fun main(args: Array<String>) {
  val cts = run(*args)
  println(cts)
}
