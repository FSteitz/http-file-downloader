/*
 * Copyright 2021 Florian Steitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.fsteitz.downloader.http

import com.github.fsteitz.downloader.http.model.EndpointConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

/**
 * @author Florian Steitz (florian@fsteitz.com)
 */
class HttpFileDownloader(private val endpointConfigDir: String) {

  private val endpointConfigFiles = mutableListOf<File>()
  private val endpointConfigs = mutableListOf<EndpointConfig>()

  fun importEndpointConfigs(): HttpFileDownloader {
    val configDir = File(endpointConfigDir)

    if (!configDir.isDirectory) {
      System.err.println("ERROR: '$endpointConfigDir' must be a directory")
      exitProcess(-1)
    }

    configDir.listFiles()?.forEach {
      println("Found config file '${it.absolutePath}' - importing it...")

      endpointConfigFiles.add(it)
      endpointConfigs.add(Json.decodeFromString(it.bufferedReader().readText()))
    }

    return this
  }

  fun startDownloads() {
    if (endpointConfigs.isEmpty()) {
      System.err.println("ERROR: 'Config files must be imported before downloads can be started")
      exitProcess(-1)
    }

    endpointConfigs.forEach { HttpEndpointFileDownloader(it).downloadAllFiles() }
  }
}