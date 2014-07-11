package com.merc.rss.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.merc.rss.R;
import com.merc.rss.account.AccountGeneral;

public class AccountSettingsActivity extends ActionBarActivity {
	private AccountManager accManager;
	private final String LOG_TAG = "ACCOUNT_SETTINGS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_settings);
		accManager = AccountManager.get(AccountSettingsActivity.this);

		findViewById(R.id.add_acc).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, showMessage("add_acc"));
				addNewAccount(AccountGeneral.ACCOUNT_TYPE,
						AccountGeneral.AUTHTOKEN_TYPE);
			}
		});
		findViewById(R.id.show_list).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, showMessage("show_list"));
				showAccountList(AccountGeneral.ACCOUNT_TYPE, true);
			}
		});
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
						AccountGeneral.AUTHTOKEN_TYPE);
			}
		});
	}

	private void addNewAccount(String accountType, String tokenType) {
		accManager.addAccount(accountType, tokenType, null, null, this,

		new AccountManagerCallback<Bundle>() {

			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					showMessage("Account was created");
				} catch (Exception e) {
					e.printStackTrace();
					showMessage("Account was not create");
				}
			}
		}, null);

	}

	private void showAccountList(final String authTokenType, boolean flag) {
		Account accounts[] = accManager
				.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		if (accounts.length < 1)
			showMessage("Accounts not found");
		else {
			for (int i = 0; i < accounts.length; i++) {

				getExistingAccountAuthToken(accounts[i],
						AccountGeneral.AUTHTOKEN_TYPE);
			}
		}

	}

	private void getExistingAccountAuthToken(Account account,
			String authTokenType) {

		accManager.getAuthToken(account, authTokenType, null, this,
				new AccountManagerCallback<Bundle>() {

					@Override
					public void run(AccountManagerFuture<Bundle> future) {
						try {
							Bundle bnd = future.getResult();
							String name = bnd
									.getString(AccountManager.KEY_ACCOUNT_NAME);
							String type = bnd
									.getString(AccountManager.KEY_ACCOUNT_TYPE);
							String authtoken = bnd
									.getString(AccountManager.KEY_AUTHTOKEN);
							showMessage("Account: "
									+ name
									+ "; type: "
									+ type
									+ ((authtoken != null) ? "; token: "
											+ authtoken : "FAIL"));
						} catch (Exception e) {
							e.printStackTrace();
							showMessage(e.getMessage());
						}
					}
				}, null);

	}

	private String showMessage(String data) {
		TextView tv = (TextView) findViewById(R.id.tokenTextView);
		String str = (String) tv.getText();
		if (str.length() < 900)
			tv.setText(tv.getText() + data + "\n");
		else
			tv.setText(data + "\n");
		return data;
	}

	private void getTokenForAccountCreateIfNeeded(String accountType,
			String authTokenType) {
		accManager.getAuthTokenByFeatures(accountType, authTokenType, null,
				this, null, null, new AccountManagerCallback<Bundle>() {
					@Override
					public void run(AccountManagerFuture<Bundle> future) {
						Bundle bnd = null;
						Log.d(LOG_TAG, "run");
						try {
							bnd = future.getResult();
							String authtoken = bnd
									.getString(AccountManager.KEY_AUTHTOKEN);
							showMessage(((authtoken != null) ? "SUCCESS!\ntoken: "
									+ authtoken
									: "FAIL"));
							Log.d("udinic", "GetTokenForAccount Bundle is "
									+ bnd);
						} catch (Exception e) {
							e.printStackTrace();
							showMessage(e.getMessage());
						}
					}
				}, null);
	}
}
