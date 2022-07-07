package com.iron.scratchpaper

import android.graphics.Path

/**
 * @author 최철훈
 * @created 2022-06-19
 * @desc
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
) {
    companion object {
        const val MODE_NORMAL = 0
        const val MODE_ERASER = 1
    }
}
