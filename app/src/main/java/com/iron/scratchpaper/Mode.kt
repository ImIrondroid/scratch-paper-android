package com.iron.scratchpaper

/**
 * @author 최철훈
 * @created 2022-07-10
 * @desc
 */
sealed class Mode {
    object Background: Mode()
    object Pen: Mode()
    object Eraser: Mode()
}