package com.iron.scratchpaper

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iron.scratchpaper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scratchPaperView: ScratchPaperView

    private var backgroundColor: Int = 0
    private var penColor: Int = 0

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
            showBottomSheetFragment(true)
        }

        binding.penImageView.setOnClickListener {
            scratchPaperView.isEraserMode = false

            binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.penImageView.setOnLongClickListener {
            showBottomSheetFragment(false)
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

    private fun showBottomSheetFragment(inBackground: Boolean) {
        val dialog = ScratchBottomSheetDialogFragment().apply {
            setOnConfirmListener { color ->
                if(inBackground) backgroundColor = color
                else penColor = color
            }
        }
        dialog.show(supportFragmentManager, ScratchBottomSheetDialogFragment.TAG)
    }
}