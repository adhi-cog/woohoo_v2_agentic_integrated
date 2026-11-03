package com.sb.woohoo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Matrix
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sb.woohoo.custom.LoggerWrapper
import com.sileria.android.Resource.getResources
import java.util.EnumMap


object QRUtils {
    fun generateQRCode(aText: String , aLogo: Int) :Bitmap?
    {
        val lUrl = aText.replace(" ","");
        try {
            val qrWidth = 400
            val qrHeight = 400
            val hintMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
            hintMap[EncodeHintType.CHARACTER_SET] = "utf-8";
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(lUrl, BarcodeFormat.QR_CODE,
                    qrWidth, qrHeight, hintMap)
            val width: Int = bitMatrix.width
            val height: Int = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height)
            {
                val offset = y * width
                for (x in 0 until width) {
                    //setting color from graphics
                    pixels[offset + x] = if (bitMatrix.get(x, y)) BLACK else WHITE
                    //setting color from color resource
//                    pixels[offset + x] = bitMatrix.get(x, y) ? ResourcesCompat.getColor(getResources(),
//                            R.color.orange,null)   : WHITE;
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            var dtcLogo : Bitmap? = null
            if(aLogo != -1){
                dtcLogo = BitmapFactory.decodeResource(getResources(), aLogo)
            }
            return mergeBitmaps(dtcLogo, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun mergeBitmaps(logo: Bitmap?, qrcode: Bitmap): Bitmap
    {
        val combined = Bitmap.createBitmap(qrcode.width, qrcode.height, qrcode.config)
        val canvas = Canvas(combined)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        canvas.drawBitmap(qrcode, Matrix(), null)

        if(logo != null){
            val resizeLogo = Bitmap.createScaledBitmap(logo,
                    canvasWidth / 5, canvasHeight / 5, true)
            val centreX = (canvasWidth - resizeLogo.width) / 2
            val centreY = (canvasHeight - resizeLogo.height) / 2
            canvas.drawBitmap(resizeLogo, centreX.toFloat(), centreY.toFloat(), null)
        }
        return combined
    }

    fun formUrl(aContext: Context, aEndPoint: String): String
    {
        val lang = LocaleHelper.getLanguage(aContext)
        val lUrl = java.lang.String.format("%s/%s/%s", Constants.BASE_URL_FOR_ST, lang, aEndPoint)
        LoggerWrapper.d(getTag(), " aEndPoint : $aEndPoint")
        LoggerWrapper.d(getTag(), " Present language : $lang")
        LoggerWrapper.d(getTag(), " Final Url Formed : $lUrl")
        return lUrl
    }

    fun getTag() : String{
        return this.javaClass.simpleName
    }
}