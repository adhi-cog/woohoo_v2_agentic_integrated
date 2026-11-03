package com.sb.woohoo.ui.main.pojo;

import com.google.gson.Gson;

/**
 * Created by Shrey on 17/03/18.
 */

public interface ParseModel<T> {

    T parseFromJson(Gson gson, String json);

}
