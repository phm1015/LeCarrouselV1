package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RowCategoryBinding;
import com.lecarrousel.model.CategoryListModel;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<CategoryListModel.Data> mArrayCategoryList;

    public CategoryAdapter(Context mContext, ArrayList<CategoryListModel.Data> mArrayCategoryList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayCategoryList = mArrayCategoryList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCategoryBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_category, parent, false);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowCategoryBinding mRowCategoryBinding;

        public ViewHolder(View itemView, RowCategoryBinding mRowCategoryBinding) {
            super(itemView);
            this.mRowCategoryBinding = mRowCategoryBinding;

            Utils.changeFont(mContext, mRowCategoryBinding.tvCategoryTitle, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowCategoryBinding.tvCategoryDesc, Utils.FontStyle.REGULAR);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CategoryListModel.Data mArrayCatList = mArrayCategoryList.get(position);
        holder.mRowCategoryBinding.tvCategoryTitle.setText(mArrayCatList.catName);
        holder.mRowCategoryBinding.tvCategoryDesc.setText(mArrayCatList.catDesc);
        Glide.with(mContext).load(mArrayCatList.catImage).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.mRowCategoryBinding.ivBackground);
        holder.mRowCategoryBinding.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, v);
            }
        });
        if (mArrayCatList.hide_name.equalsIgnoreCase("1")) {
            holder.mRowCategoryBinding.tvCategoryTitle.setVisibility(View.GONE);
        } else {
            holder.mRowCategoryBinding.tvCategoryTitle.setVisibility(View.VISIBLE);
        }
        if (mArrayCatList.hide_description.equalsIgnoreCase("1")) {
            holder.mRowCategoryBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            holder.mRowCategoryBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }
        if (holder.mRowCategoryBinding.tvCategoryDesc.getText().toString().trim().length() == 0) {
            holder.mRowCategoryBinding.tvCategoryDesc.setVisibility(View.GONE);
        } else {
            holder.mRowCategoryBinding.tvCategoryDesc.setVisibility(View.VISIBLE);
        }


    }

    public interface RecycleListClickListener {
        void onItemClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayCategoryList.size();
    }

}