package com.iron.scratchpaper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author 최철훈
 * @created 2022-06-19
 * @desc
 */
class ScratchPaperView : View {

    private var isSetUpEmbossFilter = false
    private var isSetUpBlurFilter = false
    var isEraserMode = false

    private var curShape = PATH
    private val penList: MutableList<Pen> = ArrayList()
    private var linePath = Path()
    private var eraserPath = Path()
    private val linePaint = Paint()
    private val eraserPaint = Paint()
    private var startX = -1F
    private var startY = -1F
    private var stopX = -1F
    private var stopY = -1F

    constructor(context: Context?) : super(context) {
        initializeLinePaint()
        initializeEraserPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initializeLinePaint()
        initializeEraserPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeLinePaint()
        initializeEraserPaint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in penList.indices) {
            val pen = penList[index]

            if (pen.path != null) {
                if (pen.eraseMode == Pen.MODE_ERASER) {
                    canvas.drawPath(pen.path, eraserPaint)
                } else {
                    canvas.drawPath(pen.path, linePaint)
                }
            } else {
                when (pen.type) {
                    LINE -> canvas.drawLine(pen.startX, pen.startY, pen.stopX, pen.stopY, linePaint)
                    SQUARE -> canvas.drawRect(pen.startX, pen.startY, pen.stopX, pen.stopY, linePaint)
                    CIRCLE -> {
                        val radius = sqrt(
                            (pen.stopX - pen.startX).toDouble().pow(2.0) + (pen.stopY - pen.startY).toDouble().pow(2.0)
                        ).toInt()
                        canvas.drawCircle(pen.startX, pen.startY, radius.toFloat(), linePaint)
                    }
                    PATH -> canvas.drawPath(linePath, linePaint)
                }
            }
        }

        if (isEraserMode) {
            canvas.drawPath(eraserPath, eraserPaint)
        } else {
            when (curShape) {
                LINE -> canvas.drawLine(startX, startY, stopX, stopY, linePaint)
                SQUARE -> canvas.drawRect(startX, startY, stopX, stopY, linePaint)
                CIRCLE -> {
                    val radius = sqrt(
                        (stopX - startX).toDouble().pow(2.0) + (stopY - startY).toDouble().pow(2.0)
                    ).toInt()
                    canvas.drawCircle(startX, startY, radius.toFloat(), linePaint)
                }
                PATH -> canvas.drawPath(linePath, linePaint)
            }
        }
    }

    private fun initializeLinePaint() {
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = 5f
        linePaint.style = Paint.Style.STROKE
        linePaint.color = Color.RED
    }

    private fun initializeEraserPaint() {
        eraserPaint.isAntiAlias = true
        eraserPaint.strokeWidth = 30f
        eraserPaint.style = Paint.Style.STROKE
        eraserPaint.color = Color.WHITE
    }

    fun setEmbossFilter() {
        if (isSetUpEmbossFilter) {
            linePaint.maskFilter = null
        } else {
            val embossMaskFilter = EmbossMaskFilter(floatArrayOf(2f, 2f, 2f), 0.5f, 6F, 5F)
            linePaint.maskFilter = embossMaskFilter
        }

        isSetUpEmbossFilter = !isSetUpEmbossFilter

        invalidate()
    }

    fun setBlurFilter() {
        if (isSetUpBlurFilter) {
            linePaint.maskFilter = null
        } else {
            val blurFilter = BlurMaskFilter(10F, BlurMaskFilter.Blur.NORMAL)
            linePaint.maskFilter = blurFilter
        }
        isSetUpBlurFilter = !isSetUpBlurFilter

        invalidate()
    }

    fun clear() {
        penList.clear()
        linePath.reset()
        eraserPath.reset()
        startX = -1F
        startY = -1F
        stopX = -1F
        stopY = -1F

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if((startX == -1F) and (startY == -1F)) {
                    linePath.moveTo(startX, startY)
                }

                startX = event.x
                startY = event.y

                if(curShape == PATH) {
                    if (isEraserMode) eraserPath.moveTo(startX, startY)
                    else linePath.moveTo(startX, startY)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                stopX = event.x
                stopY = event.y

                if(curShape == PATH) {
                    if (isEraserMode) eraserPath.lineTo(stopX, stopY)
                    else linePath.lineTo(stopX, stopY)
                }

                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                stopX = event.x
                stopY = event.y

                when(curShape) {
                    PATH -> {
                        if (isEraserMode) {
                            eraserPath.lineTo(stopX, stopY)
                            penList.add(
                                Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_ERASER, eraserPath)
                            )
                            eraserPath = Path()
                        } else {
                            linePath.lineTo(stopX, stopY)
                            penList.add(
                                Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_NORMAL, linePath)
                            )
                            linePath = Path()
                        }
                    }

                    else -> {
                        penList.add(
                            Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_NORMAL, null)
                        )
                    }
                }

                invalidate()
            }
        }

        return true
    }

    companion object {
        const val LINE = 1
        const val SQUARE = 2
        const val CIRCLE = 3
        const val PATH = 4
    }
}