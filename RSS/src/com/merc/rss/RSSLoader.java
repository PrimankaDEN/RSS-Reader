package com.merc.rss;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RSSLoader extends AsyncTaskLoader<Bundle> {
	public static final String URL_TAG = "url_tag";
	public static final String LOG_TAG = "RSS Loader";
	private Context context;
	private String urlToDownload;

	public static RSSLoader newInstance(Context appContext, Bundle args) {
		RSSLoader newLoader = new RSSLoader(appContext);
		newLoader.urlToDownload = args.getString(URL_TAG);
		return newLoader;
	}

	public RSSLoader(Context appContext) {
		super(appContext);
		context = appContext;
	}

	@Override
	public Bundle loadInBackground() {
		Bundle retBundle = new Bundle();
		;
		String feed = null;
		Log.d(LOG_TAG, urlToDownload);
		// try {
		try {
			URL url = new URL(urlToDownload);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			InputStream es = connection.getInputStream();
			int ret = 0;
			byte[] buf = new byte[2024];
			while ((ret = es.read(buf)) != -1) {
				os.write(buf, 0, ret);
			}
			byte[] response = os.toByteArray();
			feed = new String(response, "UTF-8");
			es.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		retBundle.putString(URL_TAG, feed);
		return retBundle;
	}

	private void viewToast(String message) {
		CharSequence text = message;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
