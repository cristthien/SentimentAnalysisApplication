import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SentimentAnalysis {

    fun analyzeSentiment(text: String, apiKey: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { // Chạy trên luồng nền (IO)
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "Analyze the sentiment of this sentence: \"$text\". Return only 'positive' or 'negative'.")
                                })
                            })
                        })
                    })
                }

                // Gửi request
                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonBody.toString())
                writer.flush()
                writer.close()

                // Nhận response
                val responseText = if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.readText()}"
                }

                // Trích xuất nội dung từ JSON phản hồi
                val sentiment = try {
                    val jsonResponse = JSONObject(responseText)
                    val candidates = jsonResponse.getJSONArray("candidates")
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    parts.getJSONObject(0).getString("text").trim() // Lấy text và loại bỏ khoảng trắng/thừa
                } catch (e: Exception) {
                    "Parsing error: ${e.message}"
                }

                callback(sentiment) // Trả về chuỗi kết quả
            } catch (e: Exception) {
                callback("Exception: ${e.message}")
            } finally {
                connection.disconnect()
            }
        }
    }

    fun analyzeSentimentPhoBERTModel(text: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { // Chạy trên luồng nền (IO)
            val url = URL("http://10.45.128.229:8000/predict")
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                // Cập nhật body yêu cầu cho FastAPI
                val jsonBody = JSONObject().apply {
                    put("sentence", text) // Truyền trực tiếp văn bản vào trường sentence
                }
                // Gửi request
                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonBody.toString())
                writer.flush()
                writer.close()

                // Nhận response
                val responseText = if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.readText()}"
                }


                // Trích xuất kết quả từ phản hồi
                val sentiment = try {
                    val jsonResponse = JSONObject(responseText)
                    val prediction = jsonResponse.getString("prediction") // Lấy kết quả dự đoán
                    prediction // Trả về dự đoán (POS, NEG, hoặc NEU)
                } catch (e: Exception) {
                    "Parsing error: ${e.message}"
                }

                callback(sentiment) // Trả về chuỗi kết quả
            } catch (e: Exception) {
                callback("Exception: ${e.message}")
            } finally {
                connection.disconnect()
            }
        }
    }
}
