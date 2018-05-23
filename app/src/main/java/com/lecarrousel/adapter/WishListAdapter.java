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
import com.lecarrousel.databinding.RecyclerFooterBinding;
import com.lecarrousel.databinding.RowWishListBinding;
import com.lecarrousel.model.WishListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;


public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private ArrayList<WishListModel.Data> mArrayWishLIst;
    private RecycleListClickListener mItemDetailClickListene;
    private SharedPrefs mPrefs;
    private boolean isLogin;

    public WishListAdapter(Context mContext, ArrayList<WishListModel.Data> mArrayWishLIst, RecycleListClickListener mItemDetailClickListene) {
        this.mContext = mContext;
        this.mArrayWishLIst = mArrayWishLIst;
        this.mItemDetailClickListene = mItemDetailClickListene;
        mPrefs = new SharedPrefs(mContext);
        isLogin = mPrefs.getBoolean(SharedPrefs.IS_LOGIN);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            RowWishListBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_wish_list, viewGroup, false);
            return new ItemViewHolder(mBinding.getRoot(), mBinding);
        } else {
            RecyclerFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recycler_footer, viewGroup, false);
            return new FooterViewHolder(footerBinding.getRoot());
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position != (mArrayWishLIst.size())) {
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder myViewHolder = (ItemViewHolder) holder;
            WishListModel.Data mArrayWishList = mArrayWishLIst.get(position);
            myViewHolder.mRowListProductBinding.tvProductName.setText(mArrayWishList.pName);
            myViewHolder.mRowListProductBinding.tvProductDesc.setText(mArrayWishList.pDesc);
            myViewHolder.mRowListProductBinding.tvPrice.setText(String.valueOf(mArrayWishList.pPrice));


            Glide.with(mContext).load(mArrayWishList.imgData.get(0).img)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(myViewHolder.mRowListProductBinding.ivProduct);
            myViewHolder.mRowListProductBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDetailClickListene.onItemDeleteClick(position, v);
                }
            });

            myViewHolder.mRowListProductBinding.layMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDetailClickListene.onItemDetailClick(position, v);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mArrayWishLIst.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public RowWishListBinding mRowListProductBinding;

        public ItemViewHolder(View itemView, RowWishListBinding mRowListProductBinding) {
            super(itemView);
            this.mRowListProductBinding = mRowListProductBinding;
            Utils.changeFont(mContext, mRowListProductBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowListProductBinding.tvProductName, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowListProductBinding.layPrice, Utils.FontStyle.BOLD);


        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface RecycleListClickListener {
        void onItemDeleteClick(int position, View view);
        void onItemDetailClick(int position, View view);

    }
}
