package com.iron.scratchpaper

/**
 * @author 최철훈
 * @created 2022-07-09
 * @desc
 */

data class PenState(
    val penList: List<Pen>,
    val penIndex: Int,
    val penColor: Int,
    val penThickness: Float,
    val eraserThickness: Float,
    val isEraserMode: Boolean,
    val isPreviousAvailable: Boolean,
    val isNextAvailable: Boolean,
    val scratchPaperColor: Int
)
