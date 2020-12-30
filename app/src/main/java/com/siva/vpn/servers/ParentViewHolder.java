package com.siva.vpn.servers;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ParentListItemExpandCollapseListener mParentListItemExpandCollapseListener;
    private boolean mExpanded;


    public interface ParentListItemExpandCollapseListener {
   void onParentListItemExpanded(int position);

        void onParentListItemCollapsed(int position);
    }

     public ParentViewHolder(View itemView) {
        super(itemView);
        mExpanded = false;
    }
   public void setMainItemClickToExpand() {
        itemView.setOnClickListener(this);
    }
   public boolean isExpanded() {
        return mExpanded;
    }

     public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

      public void onExpansionToggled(boolean expanded) {

    }
  public ParentListItemExpandCollapseListener getParentListItemExpandCollapseListener() {
        return mParentListItemExpandCollapseListener;
    }
  public void setParentListItemExpandCollapseListener(ParentListItemExpandCollapseListener parentListItemExpandCollapseListener) {
        mParentListItemExpandCollapseListener = parentListItemExpandCollapseListener;
    }

     @Override
    public void onClick(View v) {
        if (mExpanded) {
            collapseView();
        } else {
            expandView();
        }
    }

      public boolean shouldItemViewClickToggleExpansion() {
        return true;
    }

    protected void expandView() {
        setExpanded(true);
        onExpansionToggled(false);

        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemExpanded(getAdapterPosition());
        }
    }
  protected void collapseView() {
        setExpanded(false);
        onExpansionToggled(true);

        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemCollapsed(getAdapterPosition());
        }
    }
}
