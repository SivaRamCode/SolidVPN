package com.siva.vpn.servers;

import java.util.List;
public class ParentWrapper {

    private boolean mExpanded;
    private ParentListItem mParentListItem;
   public ParentWrapper(ParentListItem parentListItem) {
        mParentListItem = parentListItem;
        mExpanded = false;
    }

   public ParentListItem getParentListItem() {
        return mParentListItem;
    }

     public void setParentListItem(ParentListItem parentListItem) {
        mParentListItem = parentListItem;
    }
   public boolean isExpanded() {
        return mExpanded;
    }

      public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    public boolean isInitiallyExpanded() {
        return mParentListItem.isInitiallyExpanded();
    }

    public List<?> getChildItemList() {
        return mParentListItem.getChildItemList();
    }
}
