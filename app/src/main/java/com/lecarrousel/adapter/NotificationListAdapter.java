package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RecyclerFooterBinding;
import com.lecarrousel.databinding.RowNotificationListBinding;
import com.lecarrousel.model.NotificationModel;
import com.lecarrousel.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private ArrayList<NotificationModel.Data> mArrayNotificationLIst;
    private RecycleListClickListener mItemDetailClickListene;

    public NotificationListAdapter(Context mContext, ArrayList<NotificationModel.Data> mArrayWishLIst, RecycleListClickListener mItemDetailClickListene) {
        this.mContext = mContext;
        this.mArrayNotificationLIst = mArrayWishLIst;
        this.mItemDetailClickListene = mItemDetailClickListene;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            RowNotificationListBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_notification_list, viewGroup, false);
            return new ItemViewHolder(mBinding.getRoot(), mBinding);
        } else {
            RecyclerFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recycler_footer, viewGroup, false);
            return new FooterViewHolder(footerBinding.getRoot());
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position != (mArrayNotificationLIst.size())) {
            return TYPE_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder myViewHolder = (ItemViewHolder) holder;
            NotificationModel.Data mArrayNotificationhList = mArrayNotificationLIst.get(position);

            if (mArrayNotificationhList.notificationType.equalsIgnoreCase("2")) {
                myViewHolder.mRowNotificationListBinding.tvOrderStatus.setText(mArrayNotificationhList.notificationText);
                myViewHolder.mRowNotificationListBinding.layOrderStatus.setVisibility(View.GONE);

                myViewHolder.mRowNotificationListBinding.layMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemDetailClickListene.onItemDetailClick(position, v ,"2");
                    }
                });
            } else {
                myViewHolder.mRowNotificationListBinding.tvOrderStatus.setText(mContext.getResources().getString(R.string.order_status_changed));
                myViewHolder.mRowNotificationListBinding.layOrderStatus.setVisibility(View.VISIBLE);
                String orderStatus = mArrayNotificationhList.orderStatus;
                switch (orderStatus) {
                    case "0":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.processing_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.processing_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                        break;
                    case "1":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.delivered_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.delivered_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                        break;
                    case "2":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.cancelled_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.cancelled_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                        break;
                    case "3":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.returned_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.returned_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                        break;
                    case "4":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.out_for_delivery_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.out_for_delivery_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                        break;
                    case "5":
                        if (mArrayNotificationhList.eta.isEmpty()) {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText("("+mContext.getResources().getString(R.string.fail_to_deliver_notification)+")");
                        } else {
                            myViewHolder.mRowNotificationListBinding.tvStatus.setText(mContext.getResources().getString(R.string.delivery_format_text, mContext.getResources().getString(R.string.fail_to_deliver_notification), (mArrayNotificationhList.eta + " " + mContext.getResources().getString(R.string.hours))));
                        }
                        myViewHolder.mRowNotificationListBinding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red1));
                        break;
                }
                myViewHolder.mRowNotificationListBinding.tvOrderId.setText(mArrayNotificationhList.orderId);

                /*if(!mArrayNotificationhList.eta.isEmpty()){
                    myViewHolder.mRowNotificationListBinding.tvStatus;
                }else{
                    myViewHolder.mRowNotificationListBinding.tvEta.setVisibility(View.GONE);
                }*/

                myViewHolder.mRowNotificationListBinding.layMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemDetailClickListene.onItemDetailClick(position, v ,"1");
                    }
                });

            }
            if (mArrayNotificationhList.isRead.equalsIgnoreCase("0")) {
                myViewHolder.mRowNotificationListBinding.layMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                myViewHolder.mRowNotificationListBinding.layMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.unread));
            }
// order status : 0=>Processing,
// 1=>Delivered,
// 2=>cancelled,
// 3=>returned,
// 4=>Out for delivery
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String mDate = new Utils().changeDateFormate2(mArrayNotificationhList.updatedAt, df);
            myViewHolder.mRowNotificationListBinding.tvDate.setText(mDate);

            myViewHolder.mRowNotificationListBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDetailClickListene.onItemDeleteClick(position, v);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mArrayNotificationLIst.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public RowNotificationListBinding mRowNotificationListBinding;

        public ItemViewHolder(View itemView, RowNotificationListBinding mRowNotificationListBinding) {
            super(itemView);
            this.mRowNotificationListBinding = mRowNotificationListBinding;
            Utils.changeFont(mContext, mRowNotificationListBinding.layMain, Utils.FontStyle.REGULAR);
            Utils.changeFont(mContext, mRowNotificationListBinding.tvOrderId, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowNotificationListBinding.tvOrderStatus, Utils.FontStyle.BOLD);
            Utils.changeFont(mContext, mRowNotificationListBinding.tvStatus, Utils.FontStyle.BOLD);

        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface RecycleListClickListener {
        void onItemDeleteClick(int position, View view);

        void onItemDetailClick(int position, View view , String notifType);

    }
}
