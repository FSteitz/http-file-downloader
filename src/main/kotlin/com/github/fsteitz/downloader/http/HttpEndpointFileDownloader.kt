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

import com.github.fsteitz.downloader.http.model.Endpoint
import com.github.fsteitz.downloader.http.model.EndpointConfig
import com.github.fsteitz.downloader.http.model.HttpMethod
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * @author Florian Steitz (florian@fsteitz.com)
 */
class HttpEndpointFileDownloader(private val endpointConfig: EndpointConfig) {

  private val httpClient: HttpClient by lazy { HttpClient.newBuilder().build() }

  fun downloadAllFiles() {
    println("Downloading files of endpoint config '${endpointConfig.description}'...")
    endpointConfig.endpoints.forEachIndexed { index, endpoint -> downloadFile(index, endpoint, endpointConfig.endpoints.size) }
  }

  private fun downloadFile(endpointIndex: Int, endpoint: Endpoint, totalEndpoints: Int) {
    println("Downloading file ${endpointIndex + 1}/$totalEndpoints: '${endpoint.description}'")
    println(sendHttpRequest(endpoint.httpUrl, endpoint.httpMethod))
  }

  private fun sendHttpRequest(httpUrl: String, httpMethod: HttpMethod): String? {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(httpUrl))
        .build();

    return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
  }
}