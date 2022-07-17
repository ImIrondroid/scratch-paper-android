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
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iron.scratchpaper.databinding.DialogBottomSheetScratchBinding

/**
 * @author 최철훈
 * @created 2022-06-23
 * @desc 배경, 펜, 지우개의 색상 및 두께 설정을 위해 사용
 */
class ScratchBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private val scratchPaperViewModel: ScratchPaperViewModel by activityViewModels()

    private lateinit var binding: DialogBottomSheetScratchBinding

    private lateinit var mode: Mode
    private var color = 0
    private var thickness = 0f

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
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_COLOR, color)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.run {
            color = get(KEY_COLOR) as Int
            binding.colorPicker.setCenterPaint(color)
        }
    }

    private fun initializeView() {
        mode = scratchPaperViewModel.mode

        when (mode) {
            is Mode.Background -> {
                binding.colorTitleTextView.text = getString(R.string.scratchPaperColor)
                binding.thicknessConfirmTextView.visibility = View.GONE
            }
            is Mode.Eraser -> {
                binding.thicknessTitleTextView.text = getString(R.string.eraserThickness)
                binding.colorConstraintLayout.visibility = View.GONE
            }
            else -> {
                binding.colorTitleTextView.text = getString(R.string.penColor)
                binding.thicknessTitleTextView.text = getString(R.string.penThickness)
                binding.thicknessConfirmTextView.visibility = View.GONE
            }
        }

        color =
            when (mode) {
                is Mode.Pen -> scratchPaperViewModel.scratchPaperState.penColor
                is Mode.Eraser -> Color.WHITE
                is Mode.Background -> scratchPaperViewModel.scratchPaperState.scratchPaperColor
            }

        thickness =
            when (mode) {
                is Mode.Pen -> scratchPaperViewModel.scratchPaperState.penThickness
                is Mode.Eraser -> scratchPaperViewModel.scratchPaperState.eraserThickness
                is Mode.Background -> 0f
            }


        binding.colorPicker.apply {
            setOnColorChangeListener {
                color = it
            }
            setCenterPaint(color)
        }

        binding.colorConfirmTextView.setOnClickListener {
            when (mode) {
                is Mode.Background -> {
                    (requireActivity() as MainActivity).setStatusBarColor(color)
                    (requireActivity() as MainActivity).scratchPaperView.setScratchPaperBackgroundColor(color)
                }
                is Mode.Pen -> {
                    (requireActivity() as MainActivity).scratchPaperView.setPenColor(color)
                    (requireActivity() as MainActivity).scratchPaperView.setPenThickness(mode, thickness)
                }
            }

            dismiss()
        }

        binding.thicknessConfirmTextView.setOnClickListener {
            when (mode) {
                is Mode.Pen -> {
                    (requireActivity() as MainActivity).scratchPaperView.setPenColor(color)
                    (requireActivity() as MainActivity).scratchPaperView.setPenThickness(mode, thickness)
                }
                is Mode.Eraser -> {
                    (requireActivity() as MainActivity).scratchPaperView.setPenThickness(mode, thickness)
                }
            }

            dismiss()
        }

        when (mode) {
            is Mode.Background -> binding.thicknessConstraintLayout.visibility = View.GONE
            else -> {
                binding.seekBar.apply {
                    progress = thickness.toInt()
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
    }

    companion object {
        const val TAG = "ScratchBottomSheetDialogFragment"
        const val KEY_COLOR = "KEY_COLOR"
    }
}