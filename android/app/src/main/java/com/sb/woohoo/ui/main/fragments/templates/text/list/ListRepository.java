package com.sb.woohoo.ui.main.fragments.templates.text.list;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

public class ListRepository extends BaseRepository<ListPresenter> {

    private WooHooApiCall mApiCall;

    @Inject
    ListRepository(WooHooApiCall mApiCall) {
        this.mApiCall = mApiCall;
    }
}
