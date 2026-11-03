package com.sb.woohoo.ui.main.secondarydisplay

data class SecondaryDisplayData (val intervalTime: Long, val images: List<SecondaryDisplayImage>)
data class SecondaryDisplayImage (val image: String, val title: String? = null, val description: String? = null)