package com.sb.woohoo.ui.main.fragments.templates.text.single;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

public class TextRepository extends BaseRepository<TextPresenter> {

    private WooHooApiCall mApiCall;

    @Inject
    public TextRepository(WooHooApiCall mApiCall) {

        this.mApiCall = mApiCall;
    }
}
