package com.webview.nodebb;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;


public class WebAppApplication extends Application {
    private static WebAppApplication mInstance;

    private Tracker mTracker;


    public WebAppApplication() {
        mInstance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // http://stackoverflow.com/a/9752494
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Initialize Parse
        if (WebAppConfig.PARSE_APP_ID != null && !WebAppConfig.PARSE_APP_ID.equals("") &&
                WebAppConfig.PARSE_CLIENT_KEY != null && !WebAppConfig.PARSE_CLIENT_KEY.equals("")) {
            Parse.initialize(this, WebAppConfig.PARSE_APP_ID, WebAppConfig.PARSE_CLIENT_KEY);
        }
    }


    public static Context getContext() {
        return mInstance;
    }


    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(!WebAppConfig.ANALYTICS);
            mTracker = analytics.newTracker(R.xml.analytics_app_tracker);
        }
        return mTracker;
    }
}
