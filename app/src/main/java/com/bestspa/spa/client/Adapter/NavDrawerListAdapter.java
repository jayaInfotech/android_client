package com.bestspa.spa.client.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestspa.spa.client.Model.NavDrawerItem;
import com.bestspa.spa.client.R;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    public int getCount() {
        return this.navDrawerItems.size();
    }

    public Object getItem(int position) {
        return this.navDrawerItems.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_list_item, null);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
        ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(((NavDrawerItem) this.navDrawerItems.get(position)).getIcon());
        txtTitle.setText(((NavDrawerItem) this.navDrawerItems.get(position)).getTitle());
        if (((NavDrawerItem) this.navDrawerItems.get(position)).getCounterVisibility()) {
            txtCount.setText(((NavDrawerItem) this.navDrawerItems.get(position)).getCount());
            txtCount.setTag(((NavDrawerItem) this.navDrawerItems.get(position)).getTitle());
        } else {
            txtCount.setVisibility(8);
            txtCount.setTag(((NavDrawerItem) this.navDrawerItems.get(position)).getTitle());
        }
        return convertView;
    }
}

