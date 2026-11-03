package com.sb.woohoo.ui.main.sip;

public class LinphoneAddressForDevice {

    String room;
    String password;
    String domain;
    String proxy;
    String transport;

    public LinphoneAddressForDevice(String room,
                                    String password,
                                    String domain,
                                    String proxy,
                                    String transport) {
        this.room = room;
        this.password = password;
        this.domain = domain;
        this.proxy = proxy;
        this.transport = transport;
    }


    public String getRoom() {
        return room;
    }

    public String getPassword() {
        return password;
    }

    public String getDomain() {
        return domain;
    }

    public String getProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        return "LinphoneAddressForDevice{" +
                "room='" + room + '\'' +
                ", password='" + password + '\'' +
                ", domain='" + domain + '\'' +
                ", proxy='" + proxy + '\'' +
                ", transport='" + transport + '\'' +
                '}';
    }

    public String getTransport() { return transport; }
}
