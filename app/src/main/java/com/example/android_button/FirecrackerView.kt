package com.example.android_button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FirecrackerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private data class Particle(
        val color: Int,
        val angle: Float,
        val speed: Float,
        val size: Float,
        val lifeScale: Float
    )

    private var particles: List<Particle> = emptyList()
    private var centerX = 0f
    private var centerY = 0f
    private var progress = 0f

    fun burst(fromX: Float, fromY: Float) {
        centerX = fromX
        centerY = fromY
        progress = 0f
        particles = List(42) {
            Particle(
                color = Color.rgb(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                ),
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 180f + 120f,
                size = Random.nextFloat() * 10f + 6f,
                lifeScale = Random.nextFloat() * 0.4f + 0.6f
            )
        }

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 900
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (particles.isEmpty()) return

        val fade = 1f - progress
        particles.forEach { particle ->
            val distance = particle.speed * progress * particle.lifeScale
            val radians = Math.toRadians(particle.angle.toDouble())
            val x = centerX + (distance * cos(radians)).toFloat()
            val y = centerY + (distance * sin(radians)).toFloat()

            paint.color = particle.color
            paint.alpha = (255 * fade).toInt().coerceIn(0, 255)
            canvas.drawCircle(x, y, particle.size * fade.coerceAtLeast(0.2f), paint)
        }
    }
}
