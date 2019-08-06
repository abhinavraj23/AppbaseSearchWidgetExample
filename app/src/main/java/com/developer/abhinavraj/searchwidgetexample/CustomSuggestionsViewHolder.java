package com.developer.abhinavraj.searchwidgetexample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomSuggestionsViewHolder extends RecyclerView.ViewHolder {

    public TextView text;
    public TextView category;
    public TextView hits;
    public ImageView searchIcon;
    public ImageView trendingIcon;
    public ImageView icon;
    public LinearLayout touchListener;

    public CustomSuggestionsViewHolder(View itemView) {
        super(itemView);
        text = (TextView) itemView.findViewById(R.id.text);
        category = (TextView) itemView.findViewById(R.id.categoryText);
        hits = (TextView) itemView.findViewById(R.id.hits);
        searchIcon = (ImageView) itemView.findViewById(R.id.searchIcon);
        icon = itemView.findViewById(R.id.icon);
        trendingIcon = (ImageView) itemView.findViewById(R.id.trending_icon);
        touchListener = (LinearLayout) itemView.findViewById(R.id.touchListener);
    }
}
