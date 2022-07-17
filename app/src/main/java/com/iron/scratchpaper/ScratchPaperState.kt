package com.iron.scratchpaper

/**
 * @author 최철훈
 * @created 2022-07-09
 * @desc 현재 ScratchPaperView에 설정된 모든 데이터들의 상태를 저장하기 위한 모델
 */
data class ScratchPaperState(
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
