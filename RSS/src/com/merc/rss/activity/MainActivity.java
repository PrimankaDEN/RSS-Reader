package com.merc.rss.activity;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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

import com.merc.rss.R;
import com.merc.rss.rss.FeedParser;
import com.merc.rss.sync.SyncAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private static final int SINCABLE = 500;
	private final String LOG_TAG = "ACTIVITY";
	private final String IS_LOADED_TAG = "isLoaded";
	private final String SAVE_RSS_TAG = "saving";
	private final String AUTHORITY = "com.merc.rss";
	private final String ACCOUNT = "com.merc.rss.account";
	private final long SYNC_INTERVAL_IN_SECONDS = 300;
	private boolean isLoaded = false;
	private ListView rssListView;
	private ProgressBar progressBar;
	private String rssFeed;
	private int parsingArgument;
	private Handler rssFeedHandler = new Handler();
	private Account account;

	public void update() {
		Log.d(LOG_TAG, "update()");
		progressBar.setVisibility(View.VISIBLE);
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(account, AUTHORITY, settingsBundle);
	}

	BroadcastReceiver feedBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			rssFeed = intent.getStringExtra(SyncAdapter.FEED_TAG);
			rssFeedHandler.post(getTitleList);
			progressBar.setVisibility(View.INVISIBLE);
			Log.d(LOG_TAG, "onReceive");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			rssFeed = savedInstanceState.getString(SAVE_RSS_TAG, "");
			isLoaded = savedInstanceState.getBoolean(IS_LOADED_TAG, false);
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		rssListView = (ListView) findViewById(R.id.list);

		rssListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				parsingArgument = position;
				rssFeedHandler.post(getDescription);
			}
		});
		account = new Account(getString(R.string.app_name), ACCOUNT);

		final AccountManager accountManager = (AccountManager) this
				.getSystemService(ACCOUNT_SERVICE);

		accountManager.addAccountExplicitly(account, null, null);
		ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
		ContentResolver.setIsSyncable(account, AUTHORITY, SINCABLE);
		ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(),
				SYNC_INTERVAL_IN_SECONDS);

	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intFilt = new IntentFilter(SyncAdapter.BROADCAST_FILTER);
		registerReceiver(feedBroadcast, intFilt);
		update();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(feedBroadcast);
		ContentResolver.removePeriodicSync(account, AUTHORITY, Bundle.EMPTY);
	}

	Runnable getTitleList = new Runnable() {
		@Override
		public void run() {
			FeedParser fParser = new FeedParser(rssFeed);

			ArrayList<String> item = fParser.getTitleList();
			;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					MainActivity.this, android.R.layout.simple_list_item_1,
					item);
			adapter.notifyDataSetChanged();
			rssListView.setAdapter(adapter);
			return;
		}
	};

	Runnable getDescription = new Runnable() {
		@Override
		public void run() {
			FeedParser fParser = new FeedParser(rssFeed);
			showDescriptionActivity(fParser.getDescription(parsingArgument));
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Update:
			update();
		default:
			return super.onOptionsItemSelected(item);
		}
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
		in.putBoolean(IS_LOADED_TAG, isLoaded);
		super.onSaveInstanceState(in);
	}
}
