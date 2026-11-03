package com.sb.woohoo.ui.main.fragments.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sb.woohoo.R
import com.sb.woohoo.databinding.BluetoothBottomSheetChangenameBinding
import com.sb.woohoo.utils.others.MethodUtils


class ChangeNameBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BluetoothBottomSheetChangenameBinding? = null
    private val binding get() = _binding!!

    var mListener: DialogInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BluetoothBottomSheetChangenameBinding.inflate(inflater, container, false)
        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mBlueToothAdapter: BluetoothAdapter?

        val lBluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        mBlueToothAdapter = lBluetoothManager.adapter

        if (mBlueToothAdapter == null) {
            mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter()
        }

        // Set the current Bluetooth name if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.userInput.setText(mBlueToothAdapter?.name)
        }

        // Listen for Done action in the input field
        binding.userInput.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                val lNewName = binding.userInput.text.toString()
                updateNameCheck(mBlueToothAdapter, lNewName)
            }
            false
        }

        // Done Button Click
        binding.doneButton.setOnClickListener {
            val lNewName = binding.userInput.text.toString()
            updateNameCheck(mBlueToothAdapter, lNewName)
        }

        // Cancel Button Click
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        // Set IME options and input type for the user input field
        binding.userInput.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.userInput.setRawInputType(InputType.TYPE_CLASS_TEXT)

        // Clear Button to clear the input text
        binding.btClear.setOnClickListener {
            binding.userInput.text.clear()
        }
    }

    private fun updateNameCheck(aBluetoothManager: BluetoothAdapter?, aNewName: String) {
        if (!aNewName.equals("")) {
            updateDeviceName(aBluetoothManager, aNewName)
        } else {
            MethodUtils.showToastMessage(
                requireContext(),
                getString(R.string.bt_name_required)
            )
            return
        }
        dismiss()
    }

    private fun updateDeviceName(aBluetoothManager: BluetoothAdapter?, aNewName: String?) {
        if (!(!aNewName!!.equals("")) && aBluetoothManager?.isEnabled!!) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                aBluetoothManager.name = aNewName
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.ChangeNameBottomSheetDialog
    }

    companion object {
        val TAG = ChangeNameBottomSheet::class.toString()
    }

    fun setListener(lListener: DialogInterface) {
        mListener = lListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mListener?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
