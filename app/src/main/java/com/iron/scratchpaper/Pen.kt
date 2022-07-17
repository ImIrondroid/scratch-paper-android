package com.iron.scratchpaper

import android.graphics.Path

/**
 * @author 최철훈
 * @created 2022-06-19
 * @desc ScratchPaperView위에 그려지는 그림을 저장하기 위한 모델
 */
data class Pen(
    val startX: Float,
    val startY: Float,
    val stopX: Float,
    val stopY: Float,
    val type: Int,
    val path: Path?,
    val color: Int,
    val stroke: Float
)