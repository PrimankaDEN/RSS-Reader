package com.merc.rss.sync;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.accounts.Account;
import android.accounts.AccountManager;
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
	public static final String BROADCAST_FILTER_TAG = "sync_adapter_broadcast";
	public static final String FEED_TAG = "feed_tag";
	public static final String URL_TAG = "url";
	public static final String TOKEN_TAG = "token_tag";
	public static final String STATUS_DOWNLOAD_TAG = "download_status";
	public static final int BEGINING_DOWNLOAD = 0;
	public static final int ENDING_DOWNLOAD = 1;
	public static final int ERROR_DOWNLOAD = 2;

	private final String LOG_TAG = "SYNC_ADAPTER";
	Context context;
	AccountManager accManager;

	public SyncAdapter(Context appContext, boolean autoInitialize) {
		super(appContext, autoInitialize);
		context = appContext;
		accManager = AccountManager.get(context);
	}

	public SyncAdapter(Context appContext, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(appContext, autoInitialize, allowParallelSyncs);
		context = appContext;
	}

	private String broadcastFilter;

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		String feed = null;
		String RSS_URL = extras.getString(URL_TAG);
		//String token = extras.getString(TOKEN_TAG);
		broadcastFilter = extras.getString(BROADCAST_FILTER_TAG);
		
		//authorization error
		/*if (!token.equals(AccountGeneral.AUTHTOKEN + account.name)) {
			Intent answIntent = new Intent(broadcastFilter);
			answIntent.putExtra(STATUS_DOWNLOAD_TAG, ERROR_DOWNLOAD);
			context.sendBroadcast(answIntent);
			return;
		}*/
		
		Intent answIntent;
		answIntent = new Intent(broadcastFilter);
		answIntent.putExtra(STATUS_DOWNLOAD_TAG, BEGINING_DOWNLOAD);
		context.sendBroadcast(answIntent);
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
		answIntent = new Intent(broadcastFilter);
		answIntent.putExtra(STATUS_DOWNLOAD_TAG, ENDING_DOWNLOAD);
		answIntent.putExtra(FEED_TAG, feed);
		Log.d(LOG_TAG, "Sended");
		context.sendBroadcast(answIntent);
	}
}
