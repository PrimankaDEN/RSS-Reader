package com.merc.rss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class DescriptionActivity extends ActionBarActivity {
	public static final String DESCRIPTION_TAG = "description";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		Intent intent = getIntent();
		TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		descriptionTextView.setText(intent.getStringExtra(DESCRIPTION_TAG));
	}
}
