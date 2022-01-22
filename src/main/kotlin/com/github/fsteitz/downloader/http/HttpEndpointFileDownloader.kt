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

import com.github.fsteitz.downloader.http.model.ConfiguredUrlProcessor
import com.github.fsteitz.downloader.http.model.Endpoint
import com.github.fsteitz.downloader.http.model.EndpointConfig
import com.github.fsteitz.downloader.http.model.HttpMethod
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.system.exitProcess

/**
 * @author Florian Steitz (florian@fsteitz.com)
 */
class HttpEndpointFileDownloader(private val endpointConfig: EndpointConfig, private val targetDir: String) {

  private val httpClient: HttpClient by lazy { HttpClient.newBuilder().build() }

  fun downloadAllFiles() {
    println("Downloading files of endpoint config '${endpointConfig.description}'")
    endpointConfig.endpoints.forEachIndexed { index, endpoint -> downloadFile(index, endpoint, endpointConfig.endpoints.size) }
  }

  private fun downloadFile(endpointIndex: Int, endpoint: Endpoint, totalEndpoints: Int) {
    println("--------------------------------------------------")
    println("Preparing download of file ${endpointIndex + 1}/$totalEndpoints: '${endpoint.description}'")
    writeToDisk(sendHttpRequest(endpoint.httpUrl, endpoint.httpMethod), endpoint)
  }

  private fun sendHttpRequest(httpUrl: String, httpMethod: HttpMethod): String? {
    val processedUrl = processUrl(httpUrl, endpointConfig.urlProcessors.listIterator())
    val request = HttpRequest.newBuilder()
        .uri(URI.create(processedUrl))
        .build();

    return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
  }

  private fun processUrl(httpUrl: String, iterator: ListIterator<ConfiguredUrlProcessor>): String {
    if (iterator.hasNext()) {
      val nextIndex = iterator.nextIndex()
      val nextUrlProcessor = iterator.next()

      println("Processing step $nextIndex: '${nextUrlProcessor::class.simpleName}' processing URL '$httpUrl'")
      return processUrl(nextUrlProcessor.process(httpUrl), iterator)
    }

    println("Downloading from URL '$httpUrl'")
    return httpUrl
  }

  private fun writeToDisk(fileContent: String?, endpoint: Endpoint) {
    if (fileContent == null) {
      System.err.println("ERROR: File for endpoint '${endpoint.description} (${endpoint.httpUrl})' could not be downloaded")
      exitProcess(-1)
    }

    writeToDisk(fileContent, buildTargetFile(endpoint))
  }

  private fun writeToDisk(fileContent: String, targetFile: File) {
    if (!targetFile.exists()) {
      targetFile.createNewFile()
    }

    targetFile.writeText(fileContent)
  }

  private fun buildTargetFile(endpoint: Endpoint): File {
    val targetFileDirPath = targetDir + File.separator + endpointConfig.targetSubDir;
    val targetFileDir = File(targetFileDirPath)

    if (!targetFileDir.isDirectory) {
      System.err.println("ERROR: '$targetFileDirPath' is not a directory")
      exitProcess(-1)
    }

    return File(targetFileDirPath + File.separator + endpoint.fileName)
  }
}