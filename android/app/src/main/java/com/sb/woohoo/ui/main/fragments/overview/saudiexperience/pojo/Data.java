package com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class Data {

   @SerializedName("status")
   String status;

   @SerializedName("data")
   List<DataList> dataList;

   @SerializedName("per_page")
   int perPage;

   @SerializedName("total")
   int total;

   @SerializedName("curr_page")
   int currPage;

   @SerializedName("last_page")
   int lastPage;


    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    
    public void setDataList(List<DataList> dataList) {
        this.dataList = dataList;
    }
    public List<DataList> getDataList() {
        return dataList;
    }
    
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
    public int getPerPage() {
        return perPage;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    public int getTotal() {
        return total;
    }
    
    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }
    public int getCurrPage() {
        return currPage;
    }
    
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
    public int getLastPage() {
        return lastPage;
    }
    
}