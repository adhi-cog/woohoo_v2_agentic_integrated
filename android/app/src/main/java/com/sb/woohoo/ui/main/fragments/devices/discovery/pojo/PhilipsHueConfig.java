package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhilipsHueConfig {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("zigbeechannel")
    @Expose
    private Integer zigbeechannel;
    @SerializedName("bridgeid")
    @Expose
    private String bridgeid;
    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("dhcp")
    @Expose
    private Boolean dhcp;
    @SerializedName("ipaddress")
    @Expose
    private String ipaddress;
    @SerializedName("netmask")
    @Expose
    private String netmask;
    @SerializedName("gateway")
    @Expose
    private String gateway;
    @SerializedName("proxyaddress")
    @Expose
    private String proxyaddress;
    @SerializedName("proxyport")
    @Expose
    private Integer proxyport;
    @SerializedName("UTC")
    @Expose
    private String uTC;
    @SerializedName("localtime")
    @Expose
    private String localtime;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("modelid")
    @Expose
    private String modelid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getZigbeechannel() {
        return zigbeechannel;
    }

    public void setZigbeechannel(Integer zigbeechannel) {
        this.zigbeechannel = zigbeechannel;
    }

    public String getBridgeid() {
        return bridgeid;
    }

    public void setBridgeid(String bridgeid) {
        this.bridgeid = bridgeid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Boolean getDhcp() {
        return dhcp;
    }

    public void setDhcp(Boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getProxyaddress() {
        return proxyaddress;
    }

    public void setProxyaddress(String proxyaddress) {
        this.proxyaddress = proxyaddress;
    }

    public Integer getProxyport() {
        return proxyport;
    }

    public void setProxyport(Integer proxyport) {
        this.proxyport = proxyport;
    }

    public String getUTC() {
        return uTC;
    }

    public void setUTC(String uTC) {
        this.uTC = uTC;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }

}