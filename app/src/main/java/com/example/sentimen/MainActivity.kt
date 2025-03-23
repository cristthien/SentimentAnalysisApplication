package com.example.sentimen

import SentimentAnalysis.analyzeSentiment
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var inputText: EditText
    private lateinit var submitButton: Button
    private lateinit var emotionIcon: ImageView
    private lateinit var mainLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ các thành phần trong layout
        inputText = findViewById(R.id.inputText)
        submitButton = findViewById(R.id.submitButton)
        emotionIcon = findViewById(R.id.emotionIcon)
        mainLayout = findViewById(R.id.main)

// Xử lý sự kiện khi bấm nút gửi
        submitButton.setOnClickListener {
            val userInput = inputText.text.toString().trim() // Loại bỏ khoảng trắng thừa

            if (userInput.isNotEmpty()) {
                val apiKey = "AIzaSyDxQzpuhEUgTGcLlqVnC2Ik7HKujlz2QRE"
                analyzeSentiment(userInput, apiKey) { response ->
                    println("Response: $response") // Log kết quả trả về

                    runOnUiThread {
                        when {
                            response.isNullOrEmpty() -> {
                                // Nếu response rỗng hoặc null -> Giữ mặc định và ẩn icon
                                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                                emotionIcon.visibility = View.GONE // Ẩn icon khi không có dữ liệu
                            }
                            response.contains("positive", ignoreCase = true) -> {
                                // Nếu tích cực -> Đổi màu nền xanh, hiển thị icon happy
                                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                                emotionIcon.setImageResource(R.drawable .happy)
                                emotionIcon.visibility = View.VISIBLE
                            }
                            else -> {
                                // Nếu không tích cực -> Đổi nền đỏ, hiển thị icon sad
                                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                                emotionIcon.setImageResource(R.drawable.sad)
                                emotionIcon.visibility = View.VISIBLE
                            }

                        }
                    }
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập văn bản!", Toast.LENGTH_SHORT).show()
            }
        }

    }


}