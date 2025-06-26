package com.mobicom.s17.group8.mobicom_mco.pomo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mobicom.s17.group8.mobicom_mco.R
import kotlin.math.min

// custom view for the circular timer (cant do xml)
class CircularTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = ContextCompat.getColor(context, R.color.timer_background_gray)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.timer_progress_dark)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 120f
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_primary)
    }

    private val bounds = RectF()
    private var progress = 0.4f // placehodlre only (not yet moving)
    private var timeText = "25:00" // placeholdr

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setTime(minutes: Long, seconds: Long) {
        timeText = String.format("%02d:%02d", minutes, seconds)
        invalidate()
    }

    fun setProgressColor(colorResId: Int) {
        progressPaint.color = ContextCompat.getColor(context, colorResId)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - backgroundPaint.strokeWidth

        bounds.set(centerX - radius,
                   centerY - radius,
                  centerX + radius,
                centerY + radius)

        // draw background circle
        canvas.drawOval(bounds, backgroundPaint)

        // draw progress arc
        val sweepAngle = 360 * progress
        canvas.drawArc(bounds, -90f, sweepAngle, false, progressPaint)

        // draw time text
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(timeText, centerX, textY, textPaint)
    }
}