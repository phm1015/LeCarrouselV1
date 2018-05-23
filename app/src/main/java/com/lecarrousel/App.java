package com.lecarrousel;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.lecarrousel.utils.SharedPrefs;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Locale;

import im.ene.toro.Toro;
@ReportsCrashes(formKey = "", // will not be used
        mailTo = "bhavin@bito1.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.please_wait)

public class App extends Application {
    private static Context mContext;
    private SharedPrefs mPrefs;
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mPrefs = new SharedPrefs(mContext);
        ACRA.init(this);
        Toro.init(this);
        sApp = this;

        String language = mPrefs.getString(SharedPrefs.LANGUAGE);
        Log.e("Tag", "Lang: " + Locale.getDefault().getLanguage());
        if (language != null && language.length() > 0) {
            // Do nothing
        } else {
            String systemLanguage = Locale.getDefault().getLanguage();
            mPrefs.putString(SharedPrefs.LANGUAGE, systemLanguage);
            language = systemLanguage;
        }

        try {
            setLocale(new Locale(language));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Resources resources = mContext.getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
    }

    public static App getApp() {
        return sApp;
    }
}