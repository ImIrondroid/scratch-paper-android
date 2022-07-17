package com.iron.scratchpaper

import androidx.lifecycle.ViewModel

/**
 * @author 최철훈
 * @created 2022-07-09
 * @desc ScratchPaperView의 2가지 상태를 저장하기 위해 사용
 *       1. Mode : Background, Pen, Eraser 중 선택된 모드 저장
 *       2. ScratchPaperState : 현재 ScratchPaperView 내에 설정된 모든 데이터 저장
 */
class ScratchPaperViewModel: ViewModel() {

    lateinit var mode: Mode
    lateinit var scratchPaperState: ScratchPaperState
}