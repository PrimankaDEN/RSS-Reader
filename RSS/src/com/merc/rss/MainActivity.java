package com.merc.rss;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Bundle> {
	private final int PARSE_PAR = -500;
	private final int LOADER_ID = 500;
	private final String IS_LOADED_TAG = "isLoaded";
	private final String SAVE_RSS_TAG = "saving";
	private final String RSS_URL = "http://ria.ru/export/rss2/politics/index.xml";
	private final String TITLE = "title";
	private final String LINK = "link";
	private final String ITEM = "item";
	private final String DESCRIPTION = "description";
	private boolean isLoaded = false;
	private ListView rssListView;
	private ProgressBar progressBar;
	private String rssFeed;
	private int parsingArgument;

	@Override
	public Loader<Bundle> onCreateLoader(int arg0, Bundle arg1) {
		Bundle arguments = new Bundle();
		arguments.putString(RSSLoader.URL_TAG, RSS_URL);
		RSSLoader loader = RSSLoader.newInstance(this, arguments);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Bundle> arg0, Bundle result) {
		rssFeed = result.getString(RSSLoader.URL_TAG, "");
		parsingArgument = PARSE_PAR;
		Handler handler = new Handler();
		handler.post(getTitleList);
	}

	@Override
	public void onLoaderReset(Loader<Bundle> arg0) {

	}

	public interface CallBack {
		public void setDescription(String data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			rssFeed = savedInstanceState.getString(SAVE_RSS_TAG, "");
			isLoaded = savedInstanceState.getBoolean(IS_LOADED_TAG, false);
		}
		if (!isLoaded)
			getLoaderManager().initLoader(LOADER_ID, null, MainActivity.this)
					.forceLoad();
		else {
			parsingArgument = PARSE_PAR;
			new Thread(getTitleList).start();
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		rssListView = (ListView) findViewById(R.id.list);

		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				progressBar.setVisibility(View.VISIBLE);
				parsingArgument = position;
				new Thread(getDescription).start();
			}

		};
		rssListView.setOnItemClickListener(listener);
	}

	Runnable getTitleList = new Runnable() {
		@Override
		public void run() {

			try {
				XmlPullParserFactory factory;
				factory = XmlPullParserFactory.newInstance();
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(rssFeed));

				if (parsingArgument == PARSE_PAR) {
					ArrayList<String> item = new ArrayList<String>();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							MainActivity.this,
							android.R.layout.simple_list_item_1, item);
					adapter.notifyDataSetChanged();

					while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
						if (xpp.getEventType() == XmlPullParser.START_TAG)
							if (xpp.getName().equals(ITEM)) {
								while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
									if (xpp.getEventType() == XmlPullParser.START_TAG)
										if (xpp.getName().equals(TITLE)) {
											while (xpp.getEventType() != XmlPullParser.TEXT)
												xpp.next();
											item.add(xpp.getText());
											break;
										}
									xpp.next();
								}
							}
						xpp.next();
					}
					rssListView.setAdapter(adapter);
					progressBar.setVisibility(View.INVISIBLE);
					return;
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Runnable getDescription = new Runnable() {

		@Override
		public void run() {
			try {
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(rssFeed));

				int iter = 0;
				String description = "";
				while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
					if (xpp.getEventType() == XmlPullParser.START_TAG) {
						if (xpp.getName().equals(ITEM)) {
							if (iter == parsingArgument) {
								while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
									if (xpp.getEventType() == XmlPullParser.START_TAG)
										if (xpp.getName().equals(TITLE)
												|| xpp.getName().equals(LINK)
												|| xpp.getName().equals(
														DESCRIPTION)) {
											xpp.next();
											description += xpp.getText();
											description += "\n";
										}
									if (xpp.getEventType() == XmlPullParser.END_TAG)
										if (xpp.getName().equals(ITEM))
											break;
									xpp.next();
								}
								progressBar.setVisibility(View.INVISIBLE);
								showDescriptionActivity(description);
								return;
							}
							iter++;
						}
					}
					xpp.next();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		}

	};

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
