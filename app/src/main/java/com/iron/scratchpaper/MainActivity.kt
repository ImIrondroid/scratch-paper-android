package com.iron.scratchpaper

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iron.scratchpaper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scratchPaperView: ScratchPaperView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
    }

    private fun initialize() {
        scratchPaperView = binding.scratchPaperView
        scratchPaperView.setOnPenChangeListener { setButtonState() }

        binding.previousImageView.setOnClickListener {
            scratchPaperView.setPreviousPenList()
        }

        binding.nextImageView.setOnClickListener {
            scratchPaperView.setNextPenList()
        }

        binding.refreshImageView.setOnClickListener {
            scratchPaperView.clear()
        }

        binding.backgroundImageView.setOnClickListener {

        }

        binding.penImageView.setOnClickListener {
            scratchPaperView.isEraserMode = false

            binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.eraserImageView.setOnClickListener {
            scratchPaperView.isEraserMode = true

            binding.eraserImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.penImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.downloadImageView.setOnClickListener {

        }
    }

    private fun setButtonState() {
        when(scratchPaperView.isPreviousAvailable) {
            true -> {
                binding.previousImageView.isClickable = true
                binding.previousImageView.imageTintList = ColorStateList.valueOf(Color.BLACK)
            }
            false -> {
                binding.previousImageView.isClickable = false
                binding.previousImageView.imageTintList = ColorStateList.valueOf(Color.GRAY)
            }
        }

        when(scratchPaperView.isNextAvailable) {
            true -> {
                binding.nextImageView.isClickable = true
                binding.nextImageView.imageTintList = ColorStateList.valueOf(Color.BLACK)
            }
            false -> {
                binding.nextImageView.isClickable = false
                binding.nextImageView.imageTintList = ColorStateList.valueOf(Color.GRAY)
            }
        }
    }
}