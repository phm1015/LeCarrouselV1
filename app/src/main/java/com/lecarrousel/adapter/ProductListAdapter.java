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
import com.lecarrousel.databinding.RowListProductV2Binding;
import com.lecarrousel.model.ProductListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Bitwin on 3/9/2017.
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<ProductListModel.Data> mArrayProductList;
    private RecycleListClickListener mFavouriteClickListener;
    private SharedPrefs mPrefs;
    private boolean isLogin;

    public ProductListAdapter(Context mContext, ArrayList<ProductListModel.Data> mArrayProductList, RecycleListClickListener mItemClickListener, RecycleListClickListener mFavouriteClickListener) {
        this.mContext = mContext;
        this.mArrayProductList = mArrayProductList;
        this.mItemClickListener = mItemClickListener;
        this.mFavouriteClickListener = mFavouriteClickListener;
        mPrefs = new SharedPrefs(mContext);
        isLogin=mPrefs.getBoolean(SharedPrefs.IS_LOGIN);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType==TYPE_ITEM){
            RowListProductV2Binding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_list_product_v2, viewGroup, false);
            return new ItemViewHolder(mBinding.getRoot(), mBinding);
        }else {
            RecyclerFooterBinding footerBinding =DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recycler_footer, viewGroup, false);
            return new FooterViewHolder(footerBinding.getRoot());
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(position!=(mArrayProductList.size())){
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder myViewHolder= (ItemViewHolder) holder;
            ProductListModel.Data mArrayProdList = mArrayProductList.get(position);
            myViewHolder.mRowListProductBinding.tvProductName.setText(mArrayProdList.pName);
            myViewHolder.mRowListProductBinding.tvProductDescrp.setText(mArrayProdList.pDesc);
            myViewHolder.mRowListProductBinding.tvProductPrice.setText(String.valueOf(mArrayProdList.pPrice));

            if (mArrayProdList.isFavourite.equalsIgnoreCase("1")) {
                myViewHolder.mRowListProductBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_filled);
            } else {
                myViewHolder.mRowListProductBinding.ivProductIsFavorite.setImageResource(R.drawable.ic_heart_empty);
            }

            for(int index = 0; index<mArrayProdList.imgData.size(); index++ ){
                if(mArrayProdList.imgData.get(index).isDefault.equalsIgnoreCase("1")){

                    Glide.with(mContext).load(mArrayProdList.imgData.get(index).img)
                            .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .crossFade()
                            .into(myViewHolder.mRowListProductBinding.ivProductImg);
                }

            }

            myViewHolder.mRowListProductBinding.layMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(position, v);
                }
            });
            myViewHolder.mRowListProductBinding.ivProductIsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFavouriteClickListener.onFavouriteClick(position, v);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mArrayProductList.size()+1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public RowListProductV2Binding mRowListProductBinding;

        public ItemViewHolder(View itemView, RowListProductV2Binding mRowListProductBinding) {
            super(itemView);
            this.mRowListProductBinding = mRowListProductBinding;
            Utils.changeFont(mContext, mRowListProductBinding.tvProductName, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowListProductBinding.tvProductDescrp, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowListProductBinding.tvProductPrice, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowListProductBinding.tvProductCurrency, Utils.FontStyle.BOLD);

            if(!isLogin){
                mRowListProductBinding.ivProductIsFavorite.setVisibility(View.GONE);
            }else{
                mRowListProductBinding.ivProductIsFavorite.setVisibility(View.VISIBLE);
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder (View itemView) {
            super (itemView);
        }
    }

    public interface RecycleListClickListener {
        void onItemClick(int position, View view);
        void onFavouriteClick(int position, View view);
    }
}
