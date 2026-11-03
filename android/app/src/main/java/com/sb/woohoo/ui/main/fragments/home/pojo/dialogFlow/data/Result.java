package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.data;

import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.DialogContext;
import com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.Fulfillment;
import com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.MetaData;
import com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.Parameter;

import java.util.List;

public class Result {
    @SerializedName("source")
    private String source;
    @SerializedName("resolvedQuery")
    private String resolvedQuery;
    @SerializedName("action")
    private String action;
    @SerializedName("actionIncomplete")
    private boolean actionIncomplete;
    @SerializedName("parameters")
    private Parameter parameters;
    @SerializedName("contexts")
    private List<DialogContext> contexts;
    @SerializedName("metadata")
    private MetaData metadata;
    @SerializedName("fulfillment")
    private Fulfillment fulfillment;
    @SerializedName("score")
    private long score;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResolvedQuery() {
        return resolvedQuery;
    }

    public void setResolvedQuery(String resolvedQuery) {
        this.resolvedQuery = resolvedQuery;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isActionIncomplete() {
        return actionIncomplete;
    }

    public void setActionIncomplete(boolean actionIncomplete) {
        this.actionIncomplete = actionIncomplete;
    }

    public Parameter getParameters() {
        return parameters;
    }

    public void setParameters(Parameter parameters) {
        this.parameters = parameters;
    }

    public List<DialogContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<DialogContext> contexts) {
        this.contexts = contexts;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public Fulfillment getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(Fulfillment fulfillment) {
        this.fulfillment = fulfillment;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}