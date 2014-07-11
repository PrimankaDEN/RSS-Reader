package com.merc.rss.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {
private final String LOG_TAG ="AuthenticatorService";
	private Authenticator authenticator;

	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate();
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "onBind");
		authenticator = new Authenticator(this);
		return authenticator.getIBinder();
	}

}
