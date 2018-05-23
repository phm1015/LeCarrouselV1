package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lecarrousel.R;
import com.lecarrousel.databinding.RecyclerFooterBinding;
import com.lecarrousel.databinding.RowOrderListBinding;
import com.lecarrousel.model.OrderListModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyOrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<OrderListModel.Data> mArrayOrderList;
    private SharedPrefs mPrefs;


    public MyOrderListAdapter(Context mContext, ArrayList<OrderListModel.Data> mArrayOrderList, RecycleListClickListener mItemClickListener, RecycleListClickListener mFavouriteClickListener) {
        this.mContext = mContext;
        this.mArrayOrderList = mArrayOrderList;
        this.mItemClickListener = mItemClickListener;
        mPrefs = new SharedPrefs(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            RowOrderListBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_order_list, viewGroup, false);
            return new ItemViewHolder(mBinding.getRoot(), mBinding);
        } else {
            RecyclerFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recycler_footer, viewGroup, false);
            return new FooterViewHolder(footerBinding.getRoot());
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position != (mArrayOrderList.size())) {
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder myViewHolder = (ItemViewHolder) holder;
            OrderListModel.Data mArrayProdList = mArrayOrderList.get(position);
            myViewHolder.mRowListOrderBinding.tvOrderId.setText(String.valueOf(mArrayProdList.orderId));

            myViewHolder.mRowListOrderBinding.tvPrice.setText(String.valueOf(mArrayProdList.totalPrice));
            Glide.with(mContext).load(mArrayProdList.imgData.get(0).img).into(myViewHolder.mRowListOrderBinding.ivProduct);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = null;
            try {
                d = df.parse(mArrayProdList.createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            df = new SimpleDateFormat("dd MMM yyyy");
            myViewHolder.mRowListOrderBinding.tvOrderDate.setText(df.format(d));

            switch (mArrayProdList.status) {
                case 0:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.processing));
                    break;
                case 1:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.delivered));
                    break;
                case 2:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.canceled));
                    break;
                case 3:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.returned));
                    break;
                case 4:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.out_for_delivery));
                    break;
                case 5:
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.fail_to_deliver));
                    break;
//                case 6:
//                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext,R.color.red1));
//                    myViewHolder.mRowListOrderBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.canceled));
//                    break;
            }


        }

    }

    public int getItemCount() {
        return mArrayOrderList.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RowOrderListBinding mRowListOrderBinding;

        public ItemViewHolder(View itemView, RowOrderListBinding mRowListOrderBinding) {
            super(itemView);
            this.mRowListOrderBinding = mRowListOrderBinding;
            Utils.changeFont(mContext, mRowListOrderBinding.tvOrderId, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowListOrderBinding.tvOrderStatus, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowListOrderBinding.tvOrderDate, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowListOrderBinding.layPrice, Utils.FontStyle.BOLD);

            mRowListOrderBinding.layMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface RecycleListClickListener {
        void onItemClick(int position, View view);

    }
}
