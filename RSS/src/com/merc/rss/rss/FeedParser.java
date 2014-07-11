package com.merc.rss.rss;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class FeedParser {

	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String ITEM = "item";
	private static final String LINK = "link";
	private String feed;
	private XmlPullParser parser;

	public FeedParser(String argFeed) {
		feed = argFeed;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();

			parser = factory.newPullParser();
			parser.setInput(new StringReader(feed));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

	}

	public String getDescription(int newsIndex) {
		String description = "";
		int iter = 0;
		try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					if (parser.getName().equals(ITEM)) {
						if (iter == newsIndex) {
							while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
								if (parser.getEventType() == XmlPullParser.START_TAG)
									if (parser.getName().equals(TITLE)
											|| parser.getName().equals(LINK)
											|| parser.getName().equals(
													DESCRIPTION)) {
										parser.next();
										description += parser.getText();
										description += "\n";
									}
								if (parser.getEventType() == XmlPullParser.END_TAG)
									if (parser.getName().equals(ITEM))
										break;
								parser.next();
							}
						}
						iter++;
					}
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return description;
	}

	public ArrayList<String> getTitleList() {
		ArrayList<String> item = new ArrayList<String>();
		try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG)
					if (parser.getName().equals(ITEM)) {
						while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
							if (parser.getEventType() == XmlPullParser.START_TAG)
								if (parser.getName().equals(TITLE)) {
									while (parser.getEventType() != XmlPullParser.TEXT)
										parser.next();
									item.add(parser.getText());
									break;
								}
							parser.next();
						}
					}
				parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return item;

	}

}
