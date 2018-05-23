package com.lecarrousel.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lecarrousel.R;
import com.lecarrousel.activity.SplashActivity;
import com.lecarrousel.model.GeneralModel;
import com.lecarrousel.model.MasterDataModel;
import com.lecarrousel.retrofit.RestClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;


public class Utils {
    public static Context mContext;
    public static String SDCARD = Environment.getExternalStorageDirectory() + "/Le_Carrousel/";
    public static String EMAIL_PATTERN = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
    public static final int PERMISSION_FOR_CAMERA = 11;
    public static final String _PATTERN = "ZZZZZ";

    public Utils() {
    }

    public static void hideSoftKeyboard(Context context) {
        /**
         * Declare the View object and getting the focus of the current window
         */
        View mView = ((Activity) context).getCurrentFocus();
        /**
         * Check if mView is null or not
         */
        if (mView != null) {
            /**
             * Declare the InputMethodManager object and getting the INPUT_METHOD_SERVICE
             * service and cast to InputMethodManager
             */
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            /**
             * Request to hide the soft input window from the
             * context of the window that is currently accepting input.
             */
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(View view, Context context) {
        /**
         * Declare the View object and getting the focus of the current window
         */
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        /**
         * give focus to a specific view or to one of its descendants
         */
        view.requestFocus();
        /**
         * Explicitly request that the current input method's soft input area be shown to the user
         */
        inputMethodManager.showSoftInput(view, 0);
    }


    public static int checkNetwork(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return 1;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return 1;
            }
        }
        return 0;
    }

    public boolean isNotNull(String str) {
        return !(str == null ||
                str.equalsIgnoreCase("null") ||
                str.equalsIgnoreCase("") ||
                str.equalsIgnoreCase(" ") ||
                str.equalsIgnoreCase(null) ||
                str.trim().length() == 0);
    }

    public boolean deleteFile(String filename) {
        return new File(filename).delete();
    }


    public static void showAlertDialog(Context mActivity, String message) {
        new AlertDialog.Builder(mActivity)
                .setIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setMessage(message)
                .setPositiveButton(mActivity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    public static void showAlertDialogWithClickListener(Context mActivity, String message, DialogInterface.OnClickListener mClickListner) {
        new AlertDialog.Builder(mActivity)
                .setIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setMessage(message)
                .setPositiveButton(mActivity.getResources().getString(R.string.ok), mClickListner)
                .create()
                .show();

    }

    public static void showAlertDialogWith2ClickListener(Context mActivity, String message, DialogInterface.OnClickListener mPossitiveClickListner, DialogInterface.OnClickListener mNegativeClickListner) {
        new AlertDialog.Builder(mActivity)
                .setIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setMessage(message)
                .setPositiveButton(mActivity.getResources().getString(R.string.yes), mPossitiveClickListner)
                .setNegativeButton(mActivity.getResources().getString(R.string.no), mNegativeClickListner)
                .create()
                .show();

    }

    public static void showAlertDialogExit(final Activity mActivity, String message) {
        new AlertDialog.Builder(mActivity)
                .setIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setMessage(message)
                .setPositiveButton(mActivity.getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActivity.finishAffinity();
                    }
                })
                .setNegativeButton(mActivity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    public static void showLogoutDialog(final Activity mActivity, DialogInterface.OnClickListener mLogoutClickListener) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
        mBuilder.setTitle(mActivity.getResources().getString(R.string.logout1));
        mBuilder.setMessage(mActivity.getResources().getString(R.string.logout_msg));
        mBuilder.setPositiveButton(mActivity.getResources().getString(R.string.logout1), mLogoutClickListener);
        mBuilder.setNegativeButton(mActivity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.create().show();
    }

    public static void showLogInDialog(final Context mActivity, DialogInterface.OnClickListener mLogInClickListener) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
        mBuilder.setTitle(mActivity.getResources().getString(R.string.logout1));
        mBuilder.setMessage(mActivity.getResources().getString(R.string.need_to_login));
        mBuilder.setPositiveButton(mActivity.getResources().getString(R.string.login_pd), mLogInClickListener);
        mBuilder.setNegativeButton(mActivity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.create().show();
    }

    public String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static Dialog showProgress(Activity a) {
        Dialog dialog;
        dialog = new Dialog(a);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custome_progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setCancelable(false);
        return dialog;
    }

    public static void HideProgress(Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

    public boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str) || str.toString().equals("null");
    }

    public static void changeGravityAsPerLanguage(SharedPrefs mPref, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                changeGravityAsPerLanguage(mPref, child);
            }
        }

        if (view instanceof EditText) {
            if (mPref.getString(SharedPrefs.LANGUAGE).equalsIgnoreCase("ar")) {
                ((EditText) view).setGravity(Gravity.RIGHT);
            } else {
                ((EditText) view).setGravity(Gravity.LEFT);
            }
        }

    }

    public static enum FontStyle {
        REGULAR, BOLD
    }

    public static void changeFont(final Context context, final View v, FontStyle fontStyle) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    changeFont(context, child, fontStyle);
                }
            }
            if (fontStyle == FontStyle.REGULAR) {
                if (v instanceof TextView) {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "helveticaneue_regular.ttf"));
                } else if (v instanceof Button) {
                    ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "helveticaneue_regular.ttf"));
                } else if (v instanceof EditText) {
                    ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "helveticaneue_regular.ttf"));
                }


            }
            if (fontStyle == FontStyle.BOLD) {
                if (v instanceof TextView) {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "arialbd.ttf"));
                } else if (v instanceof Button) {
                    ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "arialbd.ttf"));
                } else if (v instanceof EditText) {
                    ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "arialbd.ttf"));
                }


            }

        } catch (Exception e) {
            /* suppress errors */
        }

    }

    public static String changeDateFormate(String _date, SimpleDateFormat your_format) throws ParseException {
//        date1="24-06-2016";
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date dt = null;
        try {
            dt = format.parse(_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String date2 = your_format.format(dt);
        Log.e("Date : ", date2);

        return date2;
    }

    public static String changeDateFormate2(String _date, SimpleDateFormat your_format) throws ParseException {
//        date1="24-06-2016";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = null;
        try {
            dt = format.parse(_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String date2 = your_format.format(dt);
        //Log.e("Date : ", date2);

        return date2;
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public static String getFormatedDate(String dateString, String outputFormat, String inputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        try {
            Date date = sdf.parse(dateString);
            String formated = new SimpleDateFormat(outputFormat).format(date);
            return formated;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressWarnings("deprecation")
    public Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
        Intent refresh = new Intent(mContext, SplashActivity.class);
        mContext.startActivity(refresh);
    }


    public static String reduceImageSize(int maxSize, int quality, Bitmap image, String folderName) {
        int mWidth, mHeight;

        if (image.getWidth() > image.getHeight()) {
            mWidth = maxSize;
            mHeight = Math.round((maxSize * image.getHeight()) / image.getWidth());
            return saveImageAsPerQuality(resizeBitmap(image, mWidth, mHeight), folderName, quality);
        } else if (image.getHeight() > image.getWidth()) {
            mHeight = maxSize;
            mWidth = Math.round((maxSize * image.getWidth()) / image.getHeight());
            return saveImageAsPerQuality(resizeBitmap(image, mWidth, mHeight), folderName, quality);
        } else if (image.getHeight() == image.getWidth() && image.getHeight() > maxSize) {
            mHeight = maxSize;
            mWidth = maxSize;
            return saveImageAsPerQuality(resizeBitmap(image, mWidth, mHeight), folderName, quality);
        } else {
            return saveImageAsPerQuality(image, folderName, quality);
        }
    }


    //Divyesh

    private static Bitmap resizeBitmap(Bitmap mBitmap, int mWidth, int mHeight) {
        return Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, false);
    }

    private static String saveImageAsPerQuality(Bitmap finalBitmap, String FolderName, int quality) {
        File myDir;
        if (FolderName.equalsIgnoreCase("")) {
            myDir = new File(SDCARD + "/saved_images");
        } else {
            myDir = new File(SDCARD + "/." + FolderName);
        }

        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image_" + n + ".jpeg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    //    Vikram
    public static MasterDataModel.COUNTRIES getCountryModelFromId(Activity mContext, int id) {
        MasterDataModel.COUNTRIES _COUNTRY = null;
        ArrayList<MasterDataModel.COUNTRIES> ARR_COUNTRY = new SharedPrefs(mContext).getCountryList();
        for (int i = 0; i < ARR_COUNTRY.size(); i++) {
            if (ARR_COUNTRY.get(i).country_id == id) {
                _COUNTRY = ARR_COUNTRY.get(i);
                _COUNTRY.spinner_position = i;
                return _COUNTRY;
            }
        }
        return _COUNTRY;
    }

    public static void showAlertDialogWithTwoClickListener(Context mActivity, String message, String mPositiveMsg, String mNagetiveMsg, DialogInterface.OnClickListener mPositiveListener, DialogInterface.OnClickListener mNagetiveListner) {
        new AlertDialog.Builder(mActivity)
                .setIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setMessage(message)
                .setPositiveButton(mPositiveMsg, mPositiveListener)
                .setNegativeButton(mNagetiveMsg, mNagetiveListner)
                .create()
                .show();

    }

    public String getTimeZone() {
        return new SimpleDateFormat(_PATTERN, Locale.getDefault()).format(System.currentTimeMillis());
    }

    public static void readNotification(Context mContext, String notificationId,retrofit2.Callback<GeneralModel> mCallBack) {
        if (Utils.checkNetwork(mContext) != 0) {
            Call<GeneralModel> requestCall = new RestClient().getApiService().readNotification(new SharedPrefs(mContext).getString(SharedPrefs.LANGUAGE), notificationId);
            if(mCallBack!=null){
                requestCall.enqueue(mCallBack);
            }else {
                requestCall.enqueue(new retrofit2.Callback<GeneralModel>() {
                    @Override
                    public void onResponse(Call<GeneralModel> call, retrofit2.Response<GeneralModel> response) {
                        if (response != null) {
                            Log.e("Tag", response.body().message);
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralModel> call, Throwable t) {
                        Log.e("error", "" + t.getMessage());
                    }
                });
            }
        }
    }
}
