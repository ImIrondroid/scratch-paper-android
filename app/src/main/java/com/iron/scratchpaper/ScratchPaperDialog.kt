package com.iron.scratchpaper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest


class ScratchPaperDialog(
    private val context: Context,
    private val onConfirm: () -> Unit
) {
    private val dialog = Dialog(context)

    init {
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_scratch_paper)
        }
    }

    fun show() {
        initializeAds()

        dialog.run {
            findViewById<TextView>(R.id.cancelTextView).setOnClickListener {
                dialog.dismiss()
            }

            findViewById<TextView>(R.id.confirmTextView).setOnClickListener {
                dialog.dismiss()
                onConfirm.invoke()
            }

            show()
        }
    }

    fun isShowing() = dialog.isShowing

    fun dismiss() {
        dialog.dismiss()
    }

    private fun initializeAds() {
        MobileAds.initialize(context)

        val adLoader: AdLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                val template: TemplateView = dialog.findViewById(R.id.nativeTemplates)
                template.setNativeAd(nativeAd)
            }
            .build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }
}