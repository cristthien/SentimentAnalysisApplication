package com.example.sentimen

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpRequestHandler {
    fun sendPostRequest(url: String, jsonBody: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            // Gửi body request
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonBody)
            writer.flush()
            writer.close()

            // Nhận phản hồi
            if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.readText()}"
            }
        } catch (e: Exception) {
            "Exception: ${e.message}"
        } finally {
            connection.disconnect()
        }
    }
}
