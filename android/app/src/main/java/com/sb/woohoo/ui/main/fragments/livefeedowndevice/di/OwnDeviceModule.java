package com.sb.woohoo.ui.main.fragments.livefeedowndevice.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.LiveFeedOwnDeviceFragment;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.OwnDevicePresenter;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.OwnDeviceRepository;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.OwnerHubListAdapter;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OwnDeviceModule {

    private LiveFeedOwnDeviceFragment mFragment;

    public OwnDeviceModule(LiveFeedOwnDeviceFragment mFragment) {
        this.mFragment = mFragment;
    }

    @Provides
    public LiveFeedOwnDeviceFragment getmFragment() {
        return mFragment;
    }

    @Provides
    public OwnerHubListAdapter.HubClickListener getListener() {
        return mFragment;
    }

    @Provides
    public Gson getGson() {
        return WooHooApp.get(mFragment.getActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    public OwnDevicePresenter getOwnDevicePresenter(OwnDeviceRepository repository) {
        return new OwnDevicePresenter(mFragment, repository);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(mFragment.getActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
