package com.iron.scratchpaper

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.iron.scratchpaper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scratchPaperView: ScratchPaperView

    private lateinit var mediaScannerConnection: MediaScannerConnection
    private lateinit var mediaScannerConnectionClient: MediaScannerConnection.MediaScannerConnectionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectMediaScanner()
        initializeView()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaScannerConnection.disconnect()
    }

    //https://ddolcat.tistory.com/824
    private fun connectMediaScanner() {
        mediaScannerConnectionClient = object: MediaScannerConnection.MediaScannerConnectionClient {
            override fun onScanCompleted(p0: String?, p1: Uri?) {
                scanFile()
            }

            override fun onMediaScannerConnected() {
                scanFile()
            }
        }

        mediaScannerConnection = MediaScannerConnection(this, mediaScannerConnectionClient)
        mediaScannerConnection.connect()
    }

    private fun scanFile() {
        val filePath = Uri.parse("file://" + Environment.getExternalStorageDirectory()).path

        filePath?.run {
            val rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val dirName = "/ScratchPaper"
            mediaScannerConnection.scanFile(rootPath + dirName, null)
        }
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
            scratchPaperView.convertBitmapToFile()
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