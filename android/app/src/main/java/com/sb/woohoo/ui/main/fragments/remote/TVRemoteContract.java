package com.sb.woohoo.ui.main.fragments.remote;

import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelData;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteData;

import java.util.List;

public interface TVRemoteContract {

    interface Views {
        void onTVRemoteMessage(String error);
        void onRemoteComponentResponse();
        void onTVRemoteDataFetched(List<TVRemoteData> list);
        void onChannelListFetched(List<TVRemoteChannelData> response);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onTVRemoteDataFetched(List<TVRemoteData> diningRestaurantList);
        void onChannelListFetched(List<TVRemoteChannelData> response);
        void onTVRemoteMessage(String error);
        void onRemoteComponentResponse();
    }
}
