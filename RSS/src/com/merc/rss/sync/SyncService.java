package com.merc.rss.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
	private SyncAdapter syncAdapter = null;
	private final String LOG_TAG = "SYNC_SERVICE";

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "onBind");
		return syncAdapter.getSyncAdapterBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
			syncAdapter = new SyncAdapter(getApplicationContext(), true);
		
	}
}
