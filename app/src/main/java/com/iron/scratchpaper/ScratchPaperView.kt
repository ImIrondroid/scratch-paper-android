package com.iron.scratchpaper

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream
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
    private var penPath = Path()
    private val penPaint = Paint()
    private var penColor = Color.RED
    private var startX = -1F
    private var startY = -1F
    private var stopX = -1F
    private var stopY = -1F

    constructor(context: Context?) : super(context) {
        initializePenPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initializePenPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializePenPaint()
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
                penPaint.color = pen.color
                penPaint.strokeWidth = pen.stroke
                canvas.drawPath(pen.path, penPaint)
            } else {
                when (pen.type) {
                    LINE -> canvas.drawLine(pen.startX, pen.startY, pen.stopX, pen.stopY, penPaint)

                    SQUARE -> canvas.drawRect(pen.startX, pen.startY, pen.stopX, pen.stopY, penPaint)

                    CIRCLE -> {
                        val radius = sqrt((pen.stopX - pen.startX).toDouble().pow(2.0) + (pen.stopY - pen.startY).toDouble().pow(2.0)).toInt()
                        canvas.drawCircle(pen.startX, pen.startY, radius.toFloat(), penPaint)
                    }

                    PATH -> {
                        penPaint.color = pen.color
                        canvas.drawPath(penPath, penPaint)
                    }
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
            penPaint.strokeWidth = 50f
            penPaint.color = Color.WHITE
            canvas.drawPath(penPath, penPaint)
        } else {
            when (curShape) {
                LINE -> canvas.drawLine(startX, startY, stopX, stopY, penPaint)

                SQUARE -> canvas.drawRect(startX, startY, stopX, stopY, penPaint)

                CIRCLE -> {
                    val radius = sqrt((stopX - startX).toDouble().pow(2.0) + (stopY - startY).toDouble().pow(2.0)).toInt()
                    canvas.drawCircle(startX, startY, radius.toFloat(), penPaint)
                }

                PATH -> {
                    penPaint.color = penColor
                    penPaint.strokeWidth = 5f
                    canvas.drawPath(penPath, penPaint)
                }
            }
        }
    }

    private fun initializePenPaint() {
        penPaint.isAntiAlias = true
        penPaint.strokeWidth = 5f
        penPaint.style = Paint.Style.STROKE
        penPaint.color = Color.RED
    }

    fun setOnPenChangeListener(onPenChangeListener: (() -> Unit)? = null) {
        this.onPenChangeListener = onPenChangeListener
    }

    fun getCurrentPenState() =
        PenState(
            penList, penIndex, isEraserMode, isPreviousAvailable, isNextAvailable, scratchPaperColor, penColor
        )

    fun savePenState(penState: PenState) {
        penList.addAll(penState.penList)
        penIndex = penState.penIndex
        isEraserMode = penState.isEraserMode
        isPreviousAvailable = penState.isPreviousAvailable
        isNextAvailable = penState.isNextAvailable
        scratchPaperColor = penState.scratchPaperColor
        penColor = penState.penColor

        invalidate()
    }


    fun saveBitmapToFile() {
        val rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val dirName = "/ScratchPaper"
        val fileName = System.currentTimeMillis().toString() + ".png"
        val folderPath = File(rootPath + dirName)
        folderPath.mkdirs()

        var outputStream: FileOutputStream? = null
        try {
            val file = File(folderPath, fileName)
            outputStream = FileOutputStream(file)

            val bitmap = drawToBitmap()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            Toast.makeText(context, "이미지가 저장되었습니다", Toast.LENGTH_SHORT).show()
        } catch (exception: Exception) {
            exception.printStackTrace()

            Toast.makeText(context, "이미지가 저장에 실패하였습니다", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream?.run {
                flush()
                close()
            }
        }
    }

    fun setScratchPaperBackgroundColor(color: Int) {
        scratchPaperColor = color
        invalidate()
    }

    fun setPenColor(color: Int) {
        penColor = color
    }

    fun setEmbossFilter() {
        if (isSetUpEmbossFilter) {
            penPaint.maskFilter = null
        } else {
            val embossMaskFilter = EmbossMaskFilter(floatArrayOf(2f, 2f, 2f), 0.5f, 6F, 5F)
            penPaint.maskFilter = embossMaskFilter
        }

        isSetUpEmbossFilter = !isSetUpEmbossFilter

        invalidate()
    }

    fun setBlurFilter() {
        if (isSetUpBlurFilter) {
            penPaint.maskFilter = null
        } else {
            val blurFilter = BlurMaskFilter(10F, BlurMaskFilter.Blur.NORMAL)
            penPaint.maskFilter = blurFilter
        }
        isSetUpBlurFilter = !isSetUpBlurFilter

        invalidate()

        drawToBitmap()
    }

    fun clear() {
        penList.clear()
        penIndex = 0

        penPath.reset()
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
                    penPath.moveTo(startX, startY)
                }

                startX = event.x
                startY = event.y

                if(curShape == PATH) {
                    penPath.moveTo(startX, startY)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                stopX = event.x
                stopY = event.y

                if(curShape == PATH) {
                    penPath.lineTo(stopX, stopY)
                }

                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                stopX = event.x
                stopY = event.y

                var pen: Pen
                when(curShape) {
                    PATH -> {
                        penPath.lineTo(stopX, stopY)
                        pen =
                            if (isEraserMode) Pen(startX, startY, stopX, stopY, curShape, penPath, Color.WHITE, 50f)
                            else Pen(startX, startY, stopX, stopY, curShape, penPath, penColor, 5f)
                        penPath = Path()
                    }

                    else -> pen = Pen(startX, startY, stopX, stopY, curShape,null, penColor, 5f)
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