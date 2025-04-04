package com.example.sentimen
import org.json.JSONObject

class JsonBodyBuilder {
    fun buildRequestBody(text: String): String {
        val jsonBody = JSONObject().apply {
            put("sentence", text) // Chuyển văn bản vào trong trường "sentence"
        }
        return jsonBody.toString()
    }
}