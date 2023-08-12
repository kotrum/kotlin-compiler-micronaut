package com.compiler.server.model

import org.jetbrains.kotlin.diagnostics.Severity

enum class ProjectSeverity {
  INFO,
  ERROR,
  WARNING;

  companion object {
    fun from(severity: Severity): ProjectSeverity {
      return when (severity) {
        Severity.ERROR -> ERROR
        Severity.INFO -> INFO
        Severity.WARNING -> WARNING
        else -> WARNING
      }
    }
  }
}