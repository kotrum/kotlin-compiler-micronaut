package com.compiler.server.configuration

import com.compiler.server.model.bean.LibrariesFile
import com.compiler.server.model.bean.VersionInfo
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import java.io.File
import javax.inject.Singleton

@Factory
class ApplicationConfiguration(
  private val librariesFolderProperties: LibrariesFolderProperties,
  private val versionInfoProperties: VersionInfoProperties
) {
  @Singleton
  fun versionInfo() = VersionInfo(
    version = versionInfoProperties.version,
    stdlibVersion = versionInfoProperties.version
  )

  @Singleton
  fun librariesFiles() = LibrariesFile(
    File(librariesFolderProperties.jvm),
    File(librariesFolderProperties.js),
    File(librariesFolderProperties.wasm)
  )
}

@ConfigurationProperties("libraries.folder")
class LibrariesFolderProperties {
  lateinit var jvm: String
  lateinit var js: String
  lateinit var wasm: String
}

@ConfigurationProperties("kotlin")
class VersionInfoProperties {
  lateinit var version: String
}
