package com.lecarrousel.customview;

/**
 * Created by mac on 10/19/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lecarrousel.R;


public class ProgressHUD extends Dialog {
    public ProgressHUD(Context context) {
        super(context);
    }

    public ProgressHUD(Context context, int theme) {
        super(context, theme);
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static ProgressHUD show(Context context, CharSequence message, boolean isShow, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener) {
        ProgressHUD dialog = new ProgressHUD(context, R.style.ProgressHUD);
        if (isShow) {
            dialog.setTitle("");
            dialog.setContentView(R.layout.progress_hud);
            if (message == null || message.length() == 0) {
                dialog.findViewById(R.id.message).setVisibility(View.GONE);
            } else {
                TextView txt = (TextView) dialog.findViewById(R.id.message);
                txt.setText(message);
            }
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(cancelListener);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            dialog.getWindow().setAttributes(lp);
            //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.show();
        } else {
            dialog.dismiss();
        }
        return dialog;
    }

    public void dismissProgressDialog(ProgressHUD progressHUD) {
        if (progressHUD.isShowing() && progressHUD != null)
            progressHUD.dismiss();
    }
}
