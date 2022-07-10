package com.iron.scratchpaper

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iron.scratchpaper.databinding.DialogBottomSheetScratchBinding

/**
 * @author 최철훈
 * @created 2022-06-23
 * @desc
 */
class ScratchBottomSheetDialogFragment(
    private val penState: PenState,
    private val mode: Mode
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomSheetScratchBinding

    private lateinit var onSelectListener: (Int, Float) -> Unit
    private var color =
        when(mode) {
            is Mode.Pen -> penState.penColor
            else -> Color.WHITE
        }
    private var thickness =
        when(mode) {
            is Mode.Pen -> penState.penThickness
            else -> penState.eraserThickness
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSheetScratchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
            }
        }
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()
    }

    private fun initializeView() {
        binding.colorPicker.setOnColorChangeListener {
            color = it
        }

        binding.selectTextView.setOnClickListener {
            when(mode) {
                is Mode.Background -> onSelectListener.invoke(color, 0f)
                is Mode.Pen -> onSelectListener.invoke(color, thickness)
                is Mode.Eraser -> onSelectListener.invoke(color, thickness)
            }

            dismiss()
        }

        when(mode) {
            is Mode.Background -> binding.seekBarConstraintLayout.visibility = View.GONE
            else -> {
                binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        thickness = p1.toFloat()
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                        p0?.run {
                            thumb = ContextCompat.getDrawable(requireContext(), R.drawable.progressbar_thumb_transparent)
                        }
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        p0?.run {
                            thumb = ContextCompat.getDrawable(requireContext(), R.drawable.progressbar_thumb_normal)
                        }
                    }
                })
            }
        }
    }

    fun setOnSelectListener(onSelectListener: (Int, Float) -> Unit) {
        this.onSelectListener = onSelectListener
    }

    companion object {
        const val TAG = "ScratchBottomSheetDialogFragment"
    }
}