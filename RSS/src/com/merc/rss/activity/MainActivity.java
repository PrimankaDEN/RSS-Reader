package com.merc.rss.activity;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.merc.rss.R;
import com.merc.rss.account.AccountGeneral;
import com.merc.rss.rss.FeedParser;
import com.merc.rss.sync.SyncAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private static final int SINCABLE = 500;
	private static final String BROADCAST_FILTER = "filter";
	private static final String ACC_NAME_TAG = "acc_name";
	private String URL = "http://feeds.bbci.co.uk/news/world/europe/rss.xml";
	private final String LOG_TAG = "ACTIVITY";
	private final String SAVE_RSS_TAG = "saving";
	private final long SYNC_INTERVAL_IN_SECONDS = 120;
	private ListView rssListView;
	private ProgressBar progressBar;
	private String rssFeed="";
	private Account account = null;
	private AccountManager accManager;

	private AccountManagerCallback<Bundle> accountManagerCallback = new AccountManagerCallback<Bundle>() {
		@Override
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				Bundle bnd = future.getResult();
				account = new Account(
						bnd.getString(AccountManager.KEY_ACCOUNT_NAME),
						bnd.getString(AccountManager.KEY_ACCOUNT_TYPE));
				token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
				setAccSettings();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	BroadcastReceiver feedBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent
					.getIntExtra(SyncAdapter.STATUS_DOWNLOAD_TAG, -1);
			if (status == SyncAdapter.BEGINING_DOWNLOAD) {
				Log.d(LOG_TAG, "begin");
				progressBar.setVisibility(View.VISIBLE);
				return;
			} else if (status == SyncAdapter.ENDING_DOWNLOAD) {
				Log.d(LOG_TAG, "end");
				rssFeed = intent.getStringExtra(SyncAdapter.FEED_TAG);
				FeedParser fParser = new FeedParser(rssFeed);
				ArrayList<String> item = fParser.getTitleList();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						item);
				adapter.notifyDataSetChanged();
				rssListView.setAdapter(adapter);
				progressBar.setVisibility(View.INVISIBLE);

				return;
			} else if (status == SyncAdapter.ERROR_DOWNLOAD) {
				Log.d(LOG_TAG, "error");
				showToast("Error. Data did not load");
				return;
			}
		}
	};

	public void update() {
		if (account == null) {
			showToast("You must create account!");
			return;
		}
		Bundle extras = new Bundle();

		extras.putString(SyncAdapter.URL_TAG, URL);
		extras.putString(SyncAdapter.BROADCAST_FILTER_TAG, BROADCAST_FILTER);
		extras.putString(SyncAdapter.TOKEN_TAG, token);
		ContentResolver.requestSync(account, AccountGeneral.AUTHORITY, extras);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String name=null;
		if (savedInstanceState != null) {
			rssFeed = savedInstanceState.getString(SAVE_RSS_TAG, "");
			name = savedInstanceState.getString(ACC_NAME_TAG, "");
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);
		rssListView = (ListView) findViewById(R.id.list);

		rssListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FeedParser fParser = new FeedParser(rssFeed);
				showDescriptionActivity(fParser.getDescription(position));
			}
		});
		accManager = AccountManager.get(this);
		if (rssFeed.isEmpty()) {
			getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
					AccountGeneral.AUTHTOKEN_TYPE);
		} else {
			account = new Account(name, AccountGeneral.ACCOUNT_TYPE);
			getExistingAccountAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE);
		}
	}

	private String token = null;

	private void getTokenForAccountCreateIfNeeded(String accountType,
			String authTokenType) {
		accManager.getAuthTokenByFeatures(accountType, authTokenType, null,
				MainActivity.this, null, null, accountManagerCallback, null);
	}

	private void getExistingAccountAuthToken(Account account,
			String authTokenType) {
		accManager.getAuthToken(account, authTokenType, null, this,
				accountManagerCallback, null);
	}

	private void setAccSettings() {

		ContentResolver.setSyncAutomatically(account, AccountGeneral.AUTHORITY,
				true);
		ContentResolver.setIsSyncable(account, AccountGeneral.AUTHORITY,
				SINCABLE);
		Bundle bundle = new Bundle();
		ContentResolver.addPeriodicSync(account, AccountGeneral.AUTHORITY,
				bundle, SYNC_INTERVAL_IN_SECONDS);
		update();
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intFilt = new IntentFilter(BROADCAST_FILTER);
		registerReceiver(feedBroadcast, intFilt);

	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(feedBroadcast);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Update: {
			update();
			break;
		}
		case R.id.AccountSettings: {
			{
				Intent intent = new Intent(MainActivity.this,
						AccountSettingsActivity.class);
				startActivity(intent);
			}
		}
		}
		return super.onOptionsItemSelected(item);

	}

	private void showDescriptionActivity(String data) {
		Intent showDescriptionIntent = new Intent(MainActivity.this,
				DescriptionActivity.class);
		showDescriptionIntent.putExtra(DescriptionActivity.DESCRIPTION_TAG,
				data);
		startActivity(showDescriptionIntent);
	}

	@Override
	protected void onSaveInstanceState(Bundle in) {
		in.putString(SAVE_RSS_TAG, rssFeed);
		if (account != null)
			in.putString(ACC_NAME_TAG, account.name);
		super.onSaveInstanceState(in);
	}

	private void showToast(String data) {
		Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
	}
}
