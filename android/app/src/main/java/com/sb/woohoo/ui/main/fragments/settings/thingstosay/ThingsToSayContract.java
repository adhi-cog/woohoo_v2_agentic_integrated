package com.sb.woohoo.ui.main.fragments.settings.thingstosay;

import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayItems;

import java.util.List;

public interface ThingsToSayContract {

    interface View {
        void displayList(List<ThingsToSayItems> list);
    }

    interface Action {
        void getList();
    }
}
