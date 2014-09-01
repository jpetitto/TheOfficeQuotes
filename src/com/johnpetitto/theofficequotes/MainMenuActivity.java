package com.johnpetitto.theofficequotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/*
 * MainMenuActivity.java is the main activity. It displays a spinner
 * that allows the user to pick quotes from a specific category. It
 * is also responsible for calling the ParseXmlQuotes class in order
 * to gather the necessary quotes and then send to the QuotesActivity
 * class via an intent.
 */

public class MainMenuActivity extends Activity {
	public final static String CATEGORY = "CATEGORY";
	private ArrayList<Quote> quotes;
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}
	
	// user has chosen a category, gather quotes and send to QuotesActivity class (if non-empty)
	public void viewQuotes(View view) {
		Spinner selectCategory = (Spinner) findViewById(R.id.selectCategory);
		String category = selectCategory.getSelectedItem().toString();
		
		// gather quotes from ParseXmlQuotes class
		quotes = new ArrayList<Quote>();
		try {
			ParseXmlQuotes.parseQuotes(category, quotes, getApplicationContext());
		} catch (XmlPullParserException e) {
			quotes = new ArrayList<Quote>();
		} catch (IOException e) {
			quotes = new ArrayList<Quote>();
		}
		
		// if no quotes were returned, notify user, otherwise send quotes to QuotesActivity class
		if (quotes.size() == 0) {
			toast = Toast.makeText(getApplicationContext(), "No quotes found", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Intent intent = new Intent(this, QuotesActivity.class);
			Collections.shuffle(quotes);
			intent.putParcelableArrayListExtra("quotes", quotes);
			startActivity(intent);
		}
	}
}
