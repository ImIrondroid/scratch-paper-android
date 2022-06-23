package com.iron.scratchpaper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * @author 최철훈
 * @created 2022-06-21
 * @desc
 */
class ColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var onColorChangeListener: ((Int) -> Unit)? = null

    private val borderPaint: Paint
    private val centerPaint: Paint
    private val mColors: IntArray = intArrayOf(
        0xFFFF0000.toInt(),
        0xFFFF00DD.toInt(),
        0xFF0000FF.toInt(),
        0xFF48FFFF.toInt(),
        0xFF41FF3A.toInt(),
        0xFFFFFF00.toInt(),
        0xFFFFFFFF.toInt(),
        0xFF8C8C8C.toInt(),
        0xFF000000.toInt(),
        0xFFFF0000.toInt()
    )

    private var mTrackingCenter = false
    private var mHighlightCenter = false

    init {
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.shader = SweepGradient(0F, 0F, mColors, null)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 70f

        centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        centerPaint.color = Color.BLACK
        centerPaint.strokeWidth = 5f
    }

    fun setOnColorChangeListener(onColorChangeListener: ((Int) -> Unit)) {
        this.onColorChangeListener = onColorChangeListener
    }

    override fun onDraw(canvas: Canvas) {
        val radius = CENTER_X - borderPaint.strokeWidth * 0.5f
        val rectF = RectF(-radius, -radius, radius, radius)
        canvas.translate(CENTER_X.toFloat(), CENTER_X.toFloat())
        canvas.drawOval(rectF, borderPaint)
        canvas.drawCircle(0f, 0f, CENTER_RADIUS.toFloat(), centerPaint)

        if (mTrackingCenter) {
            val color = centerPaint.color
            centerPaint.style = Paint.Style.STROKE

            if (mHighlightCenter) {
                centerPaint.alpha = 0xFF
            } else {
                centerPaint.alpha = 0x80
            }

            canvas.drawCircle(0f, 0f, CENTER_RADIUS + centerPaint.strokeWidth, centerPaint)

            centerPaint.style = Paint.Style.FILL
            centerPaint.color = color
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2)
    }

    private fun ave(leftColor: Int, rightColor: Int, p: Float): Int {
        return leftColor + (p * (rightColor - leftColor)).roundToInt()
    }

    private fun interpColor(colors: IntArray, unit: Float): Int {
        if (unit <= 0) return colors[0]
        if (unit >= 1) return colors[colors.size - 1]

        var p = unit * (colors.size - 1)
        val i = p.toInt()
        p -= i

        val leftColor = colors[i]
        val rightColor = colors[i + 1]
        val a = ave(Color.alpha(leftColor), Color.alpha(rightColor), p)
        val r = ave(Color.red(leftColor), Color.red(rightColor), p)
        val g = ave(Color.green(leftColor), Color.green(rightColor), p)
        val b = ave(Color.blue(leftColor), Color.blue(rightColor), p)

        return Color.argb(a, r, g, b)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - CENTER_X
        val y = event.y - CENTER_Y
        val isCenter = inCenter(x, y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTrackingCenter = isCenter

                if (isCenter) {
                    mHighlightCenter = true

                    invalidate()
                }
                if (mTrackingCenter) {
                    if (mHighlightCenter != isCenter) {
                        mHighlightCenter = isCenter

                        invalidate()
                    }
                } else {
                    val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
                    var unit = angle / (2 * PI) // 2 * PI = 360 degree
                    if (unit < 0) {
                        unit += 1f
                    }

                    centerPaint.color = interpColor(mColors, unit)

                    invalidate()
                }
            }

            MotionEvent.ACTION_MOVE -> if (mTrackingCenter) {
                if (mHighlightCenter != isCenter) {
                    mHighlightCenter = isCenter

                    invalidate()
                }
            } else {
                val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
                var unit = angle / (2 * PI) // 2 * PI = 360 degree
                if (unit < 0) {
                    unit += 1f
                }

                centerPaint.color = interpColor(mColors, unit)

                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (mTrackingCenter) {
                    if (isCenter) {
                        onColorChangeListener?.invoke(centerPaint.color)
                    }

                    mTrackingCenter = false

                    invalidate()
                }
            }
        }

        return true
    }

    private fun inCenter(x: Float, y: Float) =
        sqrt((x * x + y * y).toDouble()) <= CENTER_RADIUS

    companion object {
        private const val CENTER_X = 180
        private const val CENTER_Y = 180
        private const val CENTER_RADIUS = 50
        private const val PI = 3.1415926f
    }
}