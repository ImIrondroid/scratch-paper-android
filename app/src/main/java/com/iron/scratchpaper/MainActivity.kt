package com.iron.scratchpaper

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

        binding.refreshImageView.setOnClickListener {
            scratchPaperView.clear()
        }

        binding.penImageView.setOnClickListener {
            scratchPaperView.isEraserMode = false

            if (scratchPaperView.isEraserMode) {
                binding.penImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.eraserImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            } else {
                binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
                binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }

        binding.eraserImageView.setOnClickListener {
            scratchPaperView.isEraserMode = true

            if (scratchPaperView.isEraserMode) {
                binding.eraserImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
                binding.penImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            }
        }
    }
}