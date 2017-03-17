package com.webview.nodebb;

import android.app.Application;
import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;

public class WebAppApplication extends Application {
    private static WebAppApplication mInstance;

    private Tracker mTracker;
	private String oneSignalPlayerId;


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

        OneSignal.startInit(this).init();
		
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                setOneSignalPlayerId(userId);
            }
        });		
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
	
    public String getOneSignalPlayerId() {
        return oneSignalPlayerId;
    }

    public void setOneSignalPlayerId(String oneSignalPlayerId) {
        this.oneSignalPlayerId = oneSignalPlayerId;
    }	
}
