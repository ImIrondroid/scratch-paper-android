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
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.iron.scratchpaper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val scratchPaperViewModel: ScratchPaperViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    lateinit var scratchPaperView: ScratchPaperView

    private lateinit var mediaScannerConnection: MediaScannerConnection
    private lateinit var mediaScannerConnectionClient: MediaScannerConnection.MediaScannerConnectionClient
    private var scratchPaperDialog: ScratchPaperDialog? = null

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
            saveScratchPaperState(scratchPaperViewModel.scratchPaperState)
        }
    }

    override fun onBackPressed() {
        showFinishDialog()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        scratchPaperView.apply {
            setOnPenChangeListener { setMoveMode() }
            saveScratchPaperState(scratchPaperViewModel.scratchPaperState)
            setPenMode()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        scratchPaperViewModel.scratchPaperState = scratchPaperView.getCurrentScratchPaperState()
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
            showScratchBottomSheetFragment(Mode.Background)
        }

        binding.penImageView.setOnClickListener {
            scratchPaperView.isEraserMode = false

            setPenMode()
        }

        binding.penImageView.setOnLongClickListener {
            showScratchBottomSheetFragment(Mode.Pen)
            true
        }

        binding.eraserImageView.setOnClickListener {
            scratchPaperView.isEraserMode = true

            setPenMode()
        }

        binding.eraserImageView.setOnLongClickListener {
            showScratchBottomSheetFragment(Mode.Eraser)
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
            binding.eraserImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.penImageView.setBackgroundColor(Color.WHITE)
        } else {
            binding.eraserImageView.setBackgroundColor(Color.WHITE)
            binding.penImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
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

    fun setStatusBarColor(color: Int) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = color
        }
    }

    private fun showScratchBottomSheetFragment(mode: Mode) {
        scratchPaperViewModel.mode = mode
        scratchPaperViewModel.scratchPaperState = scratchPaperView.getCurrentScratchPaperState()

        val dialog = ScratchBottomSheetDialogFragment()
        dialog.show(supportFragmentManager, ScratchBottomSheetDialogFragment.TAG)
    }

    private fun showFinishDialog() {
        if(scratchPaperDialog == null) {
            scratchPaperDialog = ScratchPaperDialog(
                context = this,
                onConfirm = { finish() }
            )
            scratchPaperDialog?.show()
        } else {
            scratchPaperDialog?.run {
                if(isShowing())
                    dismiss()
                else
                    show()
            }
        }
    }
}