package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Data {

   @SerializedName("data")
   List<DataList> data;

    public void setData(List<DataList> data) {
        this.data = data;
    }
    public List<DataList> getData() {
        return data;
    }

}