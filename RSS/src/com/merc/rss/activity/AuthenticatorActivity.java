package com.merc.rss.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.merc.rss.R;
import com.merc.rss.account.AccountGeneral;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	private final int REQ_SIGNUP = 1;
	public final static String PARAM_USER_PASS = "USER_PASS";
	private final String LOG_TAG = "AUTHER_ACTIVITY";
	private EditText loginEditText, passEditText;
	private AccountManager accManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate");
		setContentView(R.layout.activity_authenticator);
		loginEditText = (EditText) findViewById(R.id.loginEditText);
		passEditText = (EditText) findViewById(R.id.passEditText);
		String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);

		accManager = AccountManager.get(getBaseContext());
		if (accountName != null) {
			loginEditText.setText(accountName);
		}

		findViewById(R.id.signUpButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						submit();
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onAcivityResult");
		// The sign up activity returned that the user has successfully created
		// an account
		if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
			finishLogin(data);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}

	public void submit() {
		final String userName = loginEditText.getText().toString();
		final String userPass = passEditText.getText().toString();
		final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
		Log.d(LOG_TAG, "submit");
		new AsyncTask<String, Void, Intent>() {
			@Override
			protected Intent doInBackground(String... params) {
				// Server authentication and getting auth token must be here
				// You must send login and pass to server and get token
				// authtoken = server.getToken(username, password);
				final String authtoken = AccountGeneral.AUTHTOKEN + userName;
				final Intent res = new Intent();
				res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
				res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
				res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
				res.putExtra(PARAM_USER_PASS, userPass);
				return res;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				finishLogin(intent);
			}
		}.execute();
	}

	@SuppressLint("NewApi")
	private void finishLogin(Intent intent) {
		Log.d(LOG_TAG, "finishLogin");

		String accountName = intent
				.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
		final Account account = new Account(accountName,
				intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

		if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
			Log.d(LOG_TAG, "> finishLogin > addAccountExplicitly");
			String authtoken = intent
					.getStringExtra(AccountManager.KEY_AUTHTOKEN);

			accManager.addAccountExplicitly(account, accountPassword, null);
			String tokenType=AccountGeneral.AUTHTOKEN_TYPE;

			accManager.setAuthToken(account, tokenType, authtoken);
		} else {
			Log.d(LOG_TAG, "> finishLogin > setPassword");
			accManager.setPassword(account, accountPassword);
		}

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}
}
