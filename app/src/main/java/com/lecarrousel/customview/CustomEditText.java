package com.lecarrousel.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Bitwin on 4/18/2017.
 */

public class CustomEditText extends EditText {

    private EditTextImeBackListener mOnImeBack;
    private EditTextImeNextListener mOnNextBack;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null)
                mOnImeBack.onImeBack();
            //Log.d("!!!!!!","onbackcalled");
        }
        if (event.getKeyCode() == KeyEvent.ACTION_DOWN) {
            if (mOnNextBack != null)
                mOnNextBack.mOnNextBack();
        }

        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mOnImeBack = listener;
    }

    public void setOnEditTextImeNextListener(EditTextImeNextListener listener) {
        mOnNextBack = listener;
    }

    public interface EditTextImeBackListener {
        void onImeBack();
    }

    public interface EditTextImeNextListener {
        void mOnNextBack();
    }
}
