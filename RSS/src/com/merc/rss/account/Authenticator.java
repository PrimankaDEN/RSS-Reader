package com.merc.rss.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.merc.rss.activity.AuthenticatorActivity;

//done
@SuppressLint("NewApi")
public class Authenticator extends AbstractAccountAuthenticator {
	private Context context;
	private final String LOG_TAG = "AUTHENTICATOR";
	private AccountManager accManager;

	public Authenticator(Context appContext) {
		super(appContext);
		context = appContext;
		accManager = AccountManager.get(context);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		Log.d(LOG_TAG, "addAccount");
		final Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) throws NetworkErrorException {
		Log.d(LOG_TAG, "confirmCredentials");
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		Log.d(LOG_TAG, "editProperties");
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		Log.d(LOG_TAG, "getAuthToken");
		String authToken = accManager.peekAuthToken(account, authTokenType);
		if (!authToken.isEmpty()) {
			Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}
		Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		Log.d(LOG_TAG, "getAuthTokenLabel");
		if (AccountGeneral.AUTHTOKEN_TYPE.equals(authTokenType))
			return AccountGeneral.AUTHTOKEN_TYPE_LABEL;
		else
			return authTokenType + " (Label)";
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1,
			String[] arg2) throws NetworkErrorException {
		Log.d(LOG_TAG, "hasFeatures");
		Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, String arg2, Bundle arg3)
			throws NetworkErrorException {
		Log.d(LOG_TAG, "updateCredentials");
		return null;
	}

}
