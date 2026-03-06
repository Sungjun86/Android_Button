package com.example.android_button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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
        val startX: Float,
        val startY: Float,
        val angle: Float,
        val speed: Float,
        val size: Float,
        val bornAtMs: Long,
        val lifetimeMs: Long
    )

    private val particles = mutableListOf<Particle>()
    private var celebrationEndsAtMs = 0L
    private var lastBurstAtMs = 0L
    private val burstIntervalMs = 180L

    private val ticker = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000L
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            invalidate()
        }
    }

    fun startScreenFireworks(durationMs: Long = 5_000L) {
        val now = System.currentTimeMillis()
        celebrationEndsAtMs = now + durationMs
        lastBurstAtMs = 0L
        if (!ticker.isStarted) {
            ticker.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticker.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val now = System.currentTimeMillis()

        if (now < celebrationEndsAtMs && width > 0 && height > 0 && now - lastBurstAtMs >= burstIntervalMs) {
            spawnBurst(now)
            lastBurstAtMs = now
        }

        particles.removeAll { now - it.bornAtMs >= it.lifetimeMs }

        particles.forEach { particle ->
            val lifeProgress = ((now - particle.bornAtMs).toFloat() / particle.lifetimeMs).coerceIn(0f, 1f)
            val distance = particle.speed * lifeProgress
            val radians = Math.toRadians(particle.angle.toDouble())
            val x = particle.startX + (distance * cos(radians)).toFloat()
            val y = particle.startY + (distance * sin(radians)).toFloat()

            val fade = 1f - lifeProgress
            paint.color = particle.color
            paint.alpha = (255 * fade).toInt().coerceIn(0, 255)
            canvas.drawCircle(x, y, particle.size * fade.coerceAtLeast(0.25f), paint)
        }

        if (now >= celebrationEndsAtMs && particles.isEmpty()) {
            ticker.cancel()
        }
    }

    private fun spawnBurst(now: Long) {
        val centerX = Random.nextFloat() * width
        val centerY = Random.nextFloat() * height

        repeat(30) {
            particles += Particle(
                color = Color.rgb(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                ),
                startX = centerX,
                startY = centerY,
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 240f + 120f,
                size = Random.nextFloat() * 8f + 4f,
                bornAtMs = now,
                lifetimeMs = Random.nextLong(700L, 1_300L)
            )
        }
    }
}
