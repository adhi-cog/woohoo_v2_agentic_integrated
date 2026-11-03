package com.sb.woohoo.di;


import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.ui.main.di.MainScope;
import dagger.Module;
import dagger.Provides;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

@Module
public class SocketModule {

    @WooHooAppScope
    @Provides
    Socket providesSocket() {
        Socket socket = null;
        try {
            socket = IO.socket(BuildConfig.SOCKET_URL_HUB);
        } catch (URISyntaxException uriException) {
            uriException.printStackTrace();
        }
        return socket;
    }
}