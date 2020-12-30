package com.siva.vpn.servers;

import java.util.List;
public interface ParentListItem {

       List<?> getChildItemList();
    boolean isInitiallyExpanded();
}