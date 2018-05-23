package com.lecarrousel.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lecarrousel.R;
import com.lecarrousel.adapter.GreetMsgAdapter;
import com.lecarrousel.databinding.ActGreetMsgBinding;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.utils.SharedPrefs;
import com.lecarrousel.utils.Utils;

import java.util.ArrayList;

public class GreetMessageActivity extends AppCompatActivity implements GreetMsgAdapter.RecycleListClickListener{

    private ActGreetMsgBinding mBinding;
    private Activity ACTIVITY = GreetMessageActivity.this;
    private SharedPrefs mPrefs;
    private ArrayList<MasterDataModel.Greeting_message> mArrayGreetList;
    private GreetMsgAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_greet_msg);

        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.REGULAR);

        mPrefs = new SharedPrefs(ACTIVITY);
        mBinding.layHeader.layCart.setVisibility(View.INVISIBLE);
        mBinding.layHeader.ivLeftAction.setImageDrawable(ContextCompat.getDrawable(ACTIVITY, R.drawable.ic_back));

        mArrayGreetList = mPrefs.getGreetMsgList();
        mAdapter = new GreetMsgAdapter(this,mArrayGreetList,this);
        mBinding.rvGreetList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvGreetList.setAdapter(mAdapter);

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Utils.changeFont(ACTIVITY, mBinding.layMain, Utils.FontStyle.BOLD);
    }

    @Override
    public void onGreetMsgClick(int position, View view) {
        Intent intent = new Intent();
        intent.putExtra("GreetMsg",mArrayGreetList.get(position).msg);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.enter_from_bottom,R.anim.exit_to_top);
    }
}
