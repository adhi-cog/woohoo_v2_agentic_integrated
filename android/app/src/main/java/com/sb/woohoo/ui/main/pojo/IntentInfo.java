package com.sb.woohoo.ui.main.pojo;

import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechResponse;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Shrey on 18/03/18.
 */

public class IntentInfo<T extends BaseModel> implements Serializable {

    private T intentPojo;
    private int action;
    private String category;
    private String subCategory;
    private SpeechResponse userResponse;
    private String userQuery;
    private String intentName;
    private String appUrl;
    private boolean needsExplicitFollowUp;

    private HashMap<String, String> parameters = new HashMap<>();

    public IntentInfo(T intentPojo, int action) {
        this.intentPojo = intentPojo;
        this.action = action;
    }

    public IntentInfo(int action) {
        this.action = action;
    }

    public IntentInfo() {

    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public boolean needsExplicitFollowUp() {
        return needsExplicitFollowUp;
    }

    public void setNeedsExplicitFollowUp(boolean needsExplicitFollowUp) {
        this.needsExplicitFollowUp = needsExplicitFollowUp;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public SpeechResponse getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(SpeechResponse userResponse) {
        this.userResponse = userResponse;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public T getIntentPojo() {
        return intentPojo;
    }

    public void setIntentPojo(T intentPojo) {
        this.intentPojo = intentPojo;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
