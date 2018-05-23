package com.lecarrousel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lecarrousel.R;
import com.lecarrousel.databinding.RowGreetMsgBinding;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class GreetMsgAdapter extends RecyclerView.Adapter<GreetMsgAdapter.ViewHolder> {
    private final Context mContext;
    private RecycleListClickListener mItemClickListener;
    private ArrayList<MasterDataModel.Greeting_message> mArrayGreetList;
    private SharedPrefs mPrefs;

    public GreetMsgAdapter(Context mContext, ArrayList<MasterDataModel.Greeting_message> mArrayGreetList, RecycleListClickListener mItemClickListener) {
        this.mContext = mContext;
        this.mArrayGreetList = mArrayGreetList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowGreetMsgBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_greet_msg, parent, false);
        mPrefs = new SharedPrefs(mContext);
        return new ViewHolder(mBinding.getRoot(), mBinding);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowGreetMsgBinding mRowGreetMsgBinding;

        public ViewHolder(View itemView, final RowGreetMsgBinding mRowGreetMsgBinding) {
            super(itemView);
            this.mRowGreetMsgBinding = mRowGreetMsgBinding;
            Utils.changeFont(mContext, mRowGreetMsgBinding.layMain, Utils.FontStyle.REGULAR);

            mRowGreetMsgBinding.layMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onGreetMsgClick(getAdapterPosition(),v);
                }
            });

        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MasterDataModel.Greeting_message mGreetList = mArrayGreetList.get(position);
        holder.mRowGreetMsgBinding.tvCardMsg.setText(mGreetList.msg);
    }

    public interface RecycleListClickListener {
        void onGreetMsgClick(int position, View view);
    }


    @Override
    public int getItemCount() {
        return mArrayGreetList.size();
    }


}
