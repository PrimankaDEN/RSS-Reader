package com.merc.rss.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {

private final String LOG_TAG ="Authenticator";
	public Authenticator(Context context) {
		super(context);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse arg0, String arg1,
			String arg2, String[] arg3, Bundle arg4)
			throws NetworkErrorException {
		Log.d(LOG_TAG, "addAccount");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) throws NetworkErrorException {
		Log.d(LOG_TAG, "confirmCredentials");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
		Log.d(LOG_TAG, "editProperties");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse arg0, Account arg1,
			String arg2, Bundle arg3) throws NetworkErrorException {
		Log.d(LOG_TAG, "getAuthToken");
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthTokenLabel(String arg0) {
		Log.d(LOG_TAG, "getAuthTokenLabel");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1,
			String[] arg2) throws NetworkErrorException {
		Log.d(LOG_TAG, "hasFeatures");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, String arg2, Bundle arg3)
			throws NetworkErrorException {
		Log.d(LOG_TAG, "updateCredentials");
		throw new UnsupportedOperationException();
	}

}
