package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by Shrey on 17/03/18.
 */

public abstract class BaseModel<T> {

    public T parseModel(Gson gson, String json) {
        return gson.fromJson(json, new TypeToken<T>() {
        }.getType());
    }

    public abstract void parseModel(HashMap<String, String> hashMap);
}
