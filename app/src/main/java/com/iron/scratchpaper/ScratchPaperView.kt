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

    private var onPenChangeListener: (() -> Unit)? = null

    private var isSetUpEmbossFilter = false
    private var isSetUpBlurFilter = false
    var isEraserMode = false

    private val penList: MutableList<Pen> = mutableListOf()
    private var penIndex = 0
    var isPreviousAvailable = false
    var isNextAvailable = false

    private var scratchPaperColor = Color.WHITE
    private var curShape = PATH
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

    fun setOnPenChangeListener(onPenChangeListener: (() -> Unit)? = null) {
        this.onPenChangeListener = onPenChangeListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(scratchPaperColor)

        var penCount = 0
        for (index in 0 until penList.size + penIndex) {
            if(index >= penList.size) {
                penIndex = 0
                break
            }

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

            penCount++
        }

        when (penCount) {
            0 -> {
                isPreviousAvailable = false
                isNextAvailable = true
            }
            penList.size -> {
                isPreviousAvailable = true
                isNextAvailable = false
            }
            else -> {
                isPreviousAvailable = true
                isNextAvailable = true
            }
        }

        onPenChangeListener?.run {
            if(penList.isNotEmpty()) invoke()
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

    fun setScratchPaperBackgroundColor(color: Int) {
        scratchPaperColor = color
        invalidate()
    }

    fun setPenColor(color: Int) {
        linePaint.color = color
        invalidate()
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
        penIndex = 0

        linePath.reset()
        eraserPath.reset()
        startX = -1F
        startY = -1F
        stopX = -1F
        stopY = -1F

        isPreviousAvailable = false
        isNextAvailable = false

        onPenChangeListener?.run { invoke() }

        invalidate()
    }

    fun setPreviousPenList() {
        penIndex = if(penIndex > -penList.size) (penIndex - 1) else penIndex

        invalidate()
    }

    fun setNextPenList() {
        penIndex = if(penIndex < penList.size) (penIndex + 1) else penIndex

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

                var pen: Pen
                when(curShape) {
                    PATH -> {
                        if (isEraserMode) {
                            eraserPath.lineTo(stopX, stopY)
                            pen = Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_ERASER, eraserPath)
                            eraserPath = Path()
                        } else {
                            linePath.lineTo(stopX, stopY)
                            pen = Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_NORMAL, linePath)
                            linePath = Path()
                        }
                    }

                    else -> pen = Pen(startX, startY, stopX, stopY, curShape, Pen.MODE_NORMAL, null)
                }

                if(penIndex == 0) {
                    penList.add(pen)
                } else {
                    penList[penList.size + penIndex] = pen

                    val penListSize = penList.size
                    for(index in penList.size + penIndex + 1 until penListSize) {
                        penList.removeAt(penListSize + penIndex + 1)
                    }

                    penIndex = 0
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