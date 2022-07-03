package com.iron.scratchpaper

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.iron.scratchpaper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scratchPaperView: ScratchPaperView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
    }

    private fun initializeView() {
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
            showScratchBottomSheetFragment(true)
        }

        binding.penImageView.setOnClickListener {
            scratchPaperView.isEraserMode = false

            binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.penImageView.setOnLongClickListener {
            showScratchBottomSheetFragment(false)
            true
        }

        binding.eraserImageView.setOnClickListener {
            scratchPaperView.isEraserMode = true

            binding.eraserImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.penImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.eraserImageView.setOnLongClickListener {
            true
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

    private fun showScratchBottomSheetFragment(isBackgroundMode: Boolean) {
        val dialog = ScratchBottomSheetDialogFragment().apply {
            setOnSelectListener { color ->
                if(isBackgroundMode) {
                    setStatusBarColor(color)
                    scratchPaperView.setScratchPaperBackgroundColor(color)
                } else {
                    scratchPaperView.setPenColor(color)
                }
            }
        }

        dialog.show(supportFragmentManager, ScratchBottomSheetDialogFragment.TAG)
    }

    private fun setStatusBarColor(color: Int) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = color
        }
    }
}