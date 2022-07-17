package com.iron.scratchpaper

/**
 * @author 최철훈
 * @created 2022-07-10
 * @desc ScratchPaperView에서 사용하고자하는 Mode
 *       1. Background -> ScratchPaperView 배경 색상 설정
 *       2. Pen -> ScratchPaperView에 그려지는 펜 색깔 및 두께 설정
 *       3. Eraser -> ScratchPaperView 지우개 모드 변경 및 두께 설정
 */
sealed class Mode {
    object Background: Mode()
    object Pen: Mode()
    object Eraser: Mode()
}