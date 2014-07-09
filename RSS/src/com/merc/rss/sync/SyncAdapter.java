package com.merc.rss.sync;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	public static final String KEY_FEED_ID = "com.elegion.newsfeed.sync.KEY_FEED_ID";
	public static final String BROADCAST_FILTER = "sync_adapter_broadcast";
	public static final String FEED_TAG = "feed_tag";
	private final String LOG_TAG = "SYNC_ADAPTER";
	private final String RSS_URL = "http://feeds.bbci.co.uk/news/world/europe/rss.xml";
	Context context;

	public SyncAdapter(Context appContext, boolean autoInitialize) {
		super(appContext, autoInitialize);
		context = appContext;
		Log.d(LOG_TAG, "SyncAdapter");
	}

	public SyncAdapter(Context appContext, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(appContext, autoInitialize, allowParallelSyncs);
		Log.d(LOG_TAG, "SyncAdapter");
		context = appContext;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		String feed = null;
		try {
			URL url = new URL(RSS_URL);
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
		Intent answIntent;
		answIntent = new Intent(BROADCAST_FILTER);
		answIntent.putExtra(FEED_TAG, feed);
		Log.d(LOG_TAG, "Sended");
		context.sendBroadcast(answIntent);
	}
}
