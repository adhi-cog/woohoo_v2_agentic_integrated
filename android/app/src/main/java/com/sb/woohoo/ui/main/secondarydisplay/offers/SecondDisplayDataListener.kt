package com.sb.woohoo.ui.main.secondarydisplay.offers

import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData

interface SecondDisplayDataListener {
    fun showOffers(offerList: SecondaryDisplayData)
    fun loadScreenSaversAll(screenSaverMulti : ScreenSaverMulti)
}