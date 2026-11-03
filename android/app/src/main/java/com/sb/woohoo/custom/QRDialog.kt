package com.sb.woohoo.custom

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Window
import com.sb.woohoo.databinding.DialogQrImageBinding
import com.sb.woohoo.utils.others.MethodUtils

class QRDialog(private val aActivity: Activity, private val aBitmap: Bitmap) : Dialog(aActivity)
{
    lateinit var binding: DialogQrImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogQrImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setCancelable(false)
        MethodUtils.setActivityFullScreen(aActivity)

        binding.qrLinkImg.setImageBitmap(aBitmap)
    }

}