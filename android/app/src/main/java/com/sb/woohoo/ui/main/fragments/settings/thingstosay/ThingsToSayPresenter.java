package com.sb.woohoo.ui.main.fragments.settings.thingstosay;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSay;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ThingsToSayPresenter extends BasePresenter implements ThingsToSayContract.Action {

    private ThingsToSayRepository mRepository;
    private ThingsToSayContract.View view;
    private List<ThingsToSayItems> recreatedList = new ArrayList<>(0);

    @Inject
    public ThingsToSayPresenter(ThingsToSayFragment fragment, ThingsToSayRepository thingsToSayRepository) {
        this.mRepository = thingsToSayRepository;
        this.mRepository.onAttach(this);
        view = fragment;
    }

    @Override
    public void getList() {
        mRepository.getList();
    }

    void showList(List<ThingsToSay> list) {

        for (int i = 0; i < list.size(); i++) {
            recreatedList.add(new ThingsToSayItems(list.get(i).getSkill(), true));
            for (int j = 0; j < list.get(i).getUtterance().size(); j++) {
                recreatedList.add(new ThingsToSayItems(list.get(i).getUtterance().get(j), false));
            }
        }

        view.displayList(recreatedList);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
