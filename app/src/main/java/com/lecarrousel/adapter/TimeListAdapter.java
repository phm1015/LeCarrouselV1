package com.lecarrousel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lecarrousel.R;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

/**
 * Created by bit55 on 3/4/17.
 */

public class TimeListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MasterDataModel.Delivery_estimate_time> names;

    public TimeListAdapter(Context context, ArrayList<MasterDataModel.Delivery_estimate_time> names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_dropdown, null);
        TextView tv = (TextView) convertView.findViewById(R.id.spTextView);
        View line = convertView.findViewById(R.id.line);
        line.setVisibility(View.GONE);
        tv.setText(names.get(position).title);


        Utils.changeFont(context, tv, Utils.FontStyle.REGULAR);
        return convertView;
    }
}
