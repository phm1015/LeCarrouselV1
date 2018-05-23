package com.lecarrousel.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lecarrousel.R;
import com.lecarrousel.databinding.ActPayCardBinding;
import com.lecarrousel.utils.SharedPrefs;


public class CardPaymentActivity extends Activity {
    private Activity ACTIVITY = CardPaymentActivity.this;
    private ActPayCardBinding mBinding;
    private SharedPrefs mPrefs;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    private String orderAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_pay_card);
        mPrefs = new SharedPrefs(ACTIVITY);

        mBinding.layHeader.ivLeftAction.setImageResource(R.drawable.ic_back);
        mBinding.layHeader.ivCart.setVisibility(View.GONE);

        mBinding.layHeader.ivLeftAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mBinding.webView.setWebViewClient(new YourWebClient());
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.getSettings().setUseWideViewPort(true);
        Intent intent = getIntent();
        if (intent != null) {
            orderAmount = intent.getStringExtra("orderAmount");
            mBinding.webView.loadUrl("http://lecarrousel.bitstaging.in/cybersource/payment_confirm.php?amount=" + orderAmount);
            Log.e("Tag1", orderAmount);
        }

    }

    private class YourWebClient extends WebViewClient {

        //http://lecarrousel.bitstaging.in/cybersource/confirm.php?transaction_id=5185248701306171804108
        public boolean shouldOverrideUrlLoading(WebView view, String urlConnection) {
            Log.e("Tag", "Url-:" + urlConnection);
            view.loadUrl(urlConnection);

            if (urlConnection.contains("transaction_id")) {
                String transactionId = urlConnection.split("=")[1];
                Log.e("transId", " " + transactionId);
                Intent mIntent = new Intent();
                mIntent.putExtra("transactionId", transactionId);
                setResult(RESULT_OK, mIntent);
                finish();
            }
            return true;
        }
    }

}
