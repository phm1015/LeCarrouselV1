package com.lecarrousel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lecarrousel.R;
import com.lecarrousel.model.SideMenuModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class DrawerItemCustomAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<SideMenuModel> arrDrawerList ;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ArrayList<SideMenuModel> arrDrawerList) {
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.arrDrawerList = arrDrawerList;
    }

    @Override
    public int getCount() {
        return arrDrawerList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrDrawerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(layoutResourceId, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            Utils.changeFont(mContext, viewHolder.tvName, Utils.FontStyle.BOLD);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        SideMenuModel sideMenuModel = (SideMenuModel) getItem(position);
        viewHolder.tvName.setText(sideMenuModel.name);
        viewHolder.ivIcon.setImageResource(sideMenuModel.icon);

        return convertView;
    }

    public class ViewHolder {
        private TextView tvName;
        private ImageView ivIcon;
    }
}

