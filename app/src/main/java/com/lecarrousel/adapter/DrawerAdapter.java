package com.lecarrousel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lecarrousel.R;
import com.lecarrousel.model.SideMenuModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

import static com.lecarrousel.R.id.tvName;

public class DrawerAdapter extends ArrayAdapter<SideMenuModel> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<SideMenuModel> arrDrawerList = new ArrayList<>();

    public DrawerAdapter(Context mContext, int layoutResourceId, ArrayList<SideMenuModel> arrDrawerList) {
        super(mContext, layoutResourceId, arrDrawerList);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.arrDrawerList = arrDrawerList;
    }
//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        TextView tvMenuName = (TextView) convertView.findViewById(tvName);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);


        SideMenuModel sideMenuModel = getItem(position);
        tvMenuName.setText(sideMenuModel.name);
        ivIcon.setImageResource(sideMenuModel.icon);

        Utils.changeFont(mContext, tvMenuName, Utils.FontStyle.BOLD);
        return convertView;
    }
}

