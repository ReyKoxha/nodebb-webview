package com.webview.nodebb;

import android.app.Application;
import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.webview.nodebb.R;


public class GTracker extends Application
{
	private static GTracker mInstance;

	private Tracker mTracker;


	public GTracker()
	{
		mInstance = this;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();

		try
		{
			Class.forName("android.os.AsyncTask");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

	}


	public static Context getContext()
	{
		return mInstance;
	}


	public synchronized Tracker getTracker()
	{
		if(mTracker ==null)
		{
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			analytics.setDryRun(!WebViewConfig.ANALYTICS);
			mTracker = analytics.newTracker(R.xml.analytics_app_tracker);
		}
		return mTracker;
	}
}
