package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class Pagination {

   @SerializedName("total")
   int total;

   @SerializedName("offset")
   int offset;

   @SerializedName("limit")
   int limit;


    public void setTotal(int total) {
        this.total = total;
    }
    public int getTotal() {
        return total;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public int getOffset() {
        return offset;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getLimit() {
        return limit;
    }
    
}