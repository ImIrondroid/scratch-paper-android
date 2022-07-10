package com.iron.scratchpaper

import androidx.lifecycle.ViewModel

/**
 * @author 최철훈
 * @created 2022-07-09
 * @desc PenState 저장을 위해 사용
 */
class ScratchPaperViewModel: ViewModel() {

    lateinit var scratchPaperState: ScratchPaperState
}