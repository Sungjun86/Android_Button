package com.example.android_button

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var tapCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val circleView = findViewById<View>(R.id.circleView)
        val counterText = findViewById<TextView>(R.id.counterText)
        val firecrackerView = findViewById<FirecrackerView>(R.id.firecrackerView)
        val shape = circleView.background as GradientDrawable

        counterText.text = getString(R.string.tap_count_format, tapCount)

        circleView.setOnClickListener {
            tapCount += 1
            shape.setColor(randomColor())
            counterText.text = getString(R.string.tap_count_format, tapCount)

            if (tapCount % 10 == 0) {
                firecrackerView.startScreenFireworks(durationMs = 5_000L)
            }
        }
    }

    private fun randomColor(): Int {
        return Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
    }
}
