package com.lecarrousel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lecarrousel.R;
import com.lecarrousel.model.CategoryListModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class RowSpinnerCategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CategoryListModel.Types> names;

    public RowSpinnerCategoryAdapter(Context context, ArrayList<CategoryListModel.Types> types) {
        this.context = context;
        this.names = types;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_dropdown, null);
        TextView tv = (TextView) convertView.findViewById(R.id.spTextView);
        View line = convertView.findViewById(R.id.line);
        line.setVisibility(View.GONE);
        tv.setText(names.get(position).typeName);


        Utils.changeFont(context, tv, Utils.FontStyle.REGULAR);
        return convertView;
    }
}
