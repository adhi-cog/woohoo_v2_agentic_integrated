package com.sb.woohoo.custom.smileyrating.smileys;

import android.graphics.Color;

import com.sb.woohoo.custom.smilerating.Point;
import com.sb.woohoo.custom.smileyrating.smileys.base.Smiley;

public class Okay extends Smiley {

    public Okay(String mName) {
        super(-135, 360);
        createStraightSmile(new Point(CENTER_SMILE, MOUTH_CENTER_Y), (CENTER_SMILE * 0.1f), 350f, (CENTER_SMILE * 0.9f));
        setup(
                mName,
                Color.parseColor("#f2dd68"),
                Color.parseColor("#353431")
        );
    }

}
