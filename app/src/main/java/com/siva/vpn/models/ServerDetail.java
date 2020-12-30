package com.siva.vpn.models;

public class ServerDetail {
    private String servername;
    private String serverip;
    private String serverflag;
    private String reward_server;
    private String isPaid;
    private String category;
    private String isDefault;

    public ServerDetail() {
    }

    public ServerDetail(String servername, String serverip, String serverflag, String reward_server, String isPaid,String category,String isDefault) {
        this.servername = servername;
        this.serverip = serverip;
        this.serverflag = serverflag;
        this.reward_server = reward_server;
        this.isPaid = isPaid;
        this.category=category;
        this.isDefault=isDefault;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public String getServerflag() {
        return serverflag;
    }

    public void setServerflag(String serverflag) {
        this.serverflag = serverflag;
    }

    public String getReward_server() {
        return reward_server;
    }

    public void setReward_server(String reward_server) {
        this.reward_server = reward_server;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }
}
