package com.iron.scratchpaper

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iron.scratchpaper.databinding.DialogBottomSheetScratchBinding

/**
 * @author 최철훈
 * @created 2022-06-23
 * @desc
 */
class ScratchBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var onConfirmListener: (Int) -> Unit
    private lateinit var binding: DialogBottomSheetScratchBinding

    private var color = 0

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
            onConfirmListener.invoke(color)
            dismiss()
        }
    }

    fun setOnConfirmListener(onConfirmListener: (Int) -> Unit) {
        this.onConfirmListener = onConfirmListener
    }

    companion object {
        const val TAG = "ScratchBottomSheetDialogFragment"
    }
}