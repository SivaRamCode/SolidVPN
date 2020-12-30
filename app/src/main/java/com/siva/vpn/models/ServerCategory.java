package com.siva.vpn.models;

import com.siva.vpn.servers.ParentListItem;

import java.util.List;

public class ServerCategory implements ParentListItem {
    private String mName;
    private List<ServerDetail> serverDetail;
    private String mCategoryFlag;
    public ServerCategory(String name, List<ServerDetail> ServerDetail,String CategoryFlag) {
        mName = name;
        serverDetail = ServerDetail;
        mCategoryFlag=CategoryFlag;
    }

    public String getName() {
        return mName;
    }

    public String getCategoryFlag() {
        return mCategoryFlag;
    }

    public List<ServerDetail> getServerDetail() {
        return serverDetail;
    }

    @Override
    public List<?> getChildItemList() {
        return serverDetail;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
