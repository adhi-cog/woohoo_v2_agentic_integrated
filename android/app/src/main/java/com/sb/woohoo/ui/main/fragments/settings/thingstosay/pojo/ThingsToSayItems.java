package com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo;

public class ThingsToSayItems {

    private String name;

    private boolean header;

    public ThingsToSayItems(String name, boolean header) {
        this.name = name;
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }


}
