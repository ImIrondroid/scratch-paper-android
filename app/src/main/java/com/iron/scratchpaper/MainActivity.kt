package com.iron.scratchpaper

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.iron.scratchpaper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val penViewModel: PenViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var scratchPaperView: ScratchPaperView

    private lateinit var mediaScannerConnection: MediaScannerConnection
    private lateinit var mediaScannerConnectionClient: MediaScannerConnection.MediaScannerConnectionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
        initializeAdMob()
    }

    override fun onRestart() {
        super.onRestart()

        scratchPaperView.apply {
            setOnPenChangeListener { setMoveMode() }
            savePenState(penViewModel.penState)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        scratchPaperView.apply {
            setOnPenChangeListener { setMoveMode() }
            savePenState(penViewModel.penState)
            setPenMode()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        penViewModel.penState = scratchPaperView.getCurrentPenState()
    }

    private fun initializeView() {
        scratchPaperView = binding.scratchPaperView
        scratchPaperView.setOnPenChangeListener { setMoveMode() }

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

            setPenMode()
        }

        binding.penImageView.setOnLongClickListener {
            showScratchBottomSheetFragment(false)
            true
        }

        binding.eraserImageView.setOnClickListener {
            scratchPaperView.isEraserMode = true

            setPenMode()
        }

        binding.eraserImageView.setOnLongClickListener {
            true
        }

        binding.downloadImageView.setOnClickListener {
            scratchPaperView.saveBitmapToFile()
            connectMediaScanner()
        }
    }

    private fun initializeAdMob() {
        MobileAds.initialize(this) { Unit }

        val adRequest = AdRequest
            .Builder()
            .build()

        binding.adView.apply {
            adListener = object: AdListener() {
                override fun onAdClicked() {
                    binding.adView.visibility = View.GONE
                }
            }
            loadAd(adRequest)
        }
    }

    private fun connectMediaScanner() {
        mediaScannerConnectionClient = object: MediaScannerConnection.MediaScannerConnectionClient {
            override fun onScanCompleted(p0: String?, p1: Uri?) {
                mediaScannerConnection.disconnect()
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

    private fun setPenMode() {
        if(scratchPaperView.isEraserMode) {
            binding.eraserImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
            binding.penImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
            binding.eraserImageView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.penImageView.setBackgroundColor(Color.parseColor("#EAEAEA"))
        }
    }

    private fun setMoveMode() {
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

    private fun setStatusBarColor(color: Int) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = color
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
}