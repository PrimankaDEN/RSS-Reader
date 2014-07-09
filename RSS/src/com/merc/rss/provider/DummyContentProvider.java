package com.merc.rss.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DummyContentProvider extends ContentProvider {
	private final String LOG_TAG = "DUMMY_CONTENT_PROVIDER";

	public DummyContentProvider() {
		Log.d(LOG_TAG, "DummyContentProvider");
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		Log.d(LOG_TAG, "delete");
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		Log.d(LOG_TAG, "getType");
		return new String();
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		Log.d(LOG_TAG, "insert");
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "onCreate");
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		Log.d(LOG_TAG, " query");
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		Log.d(LOG_TAG, "update");
		return 0;
	}

}
