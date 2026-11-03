package com.sb.woohoo.ui.main.fragments.templates.text.list.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.templates.text.list.ItemAdapter;
import com.sb.woohoo.ui.main.fragments.templates.text.list.ListFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.Module;
import dagger.Provides;

@Module
public class ListModule {

    private ListFragment mFragment;

    public ListModule(ListFragment listFragment) {
        this.mFragment = listFragment;
    }


    @Provides
    public ListFragment getListFragment() {
        return mFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(mFragment.getContext(), RecyclerView.VERTICAL, false);
    }

    @Provides
    public ItemAdapter getItemAdapter(ListFragment mFragment) {
        return new ItemAdapter(mFragment);
    }
}
