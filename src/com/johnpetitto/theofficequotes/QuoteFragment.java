package com.johnpetitto.theofficequotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * QuotesActivity.java is responsible for displaying an individual quote
 * within a fragment. This class is used by the QuotesActivity class when 
 * constructing the ViewPager.
 */

public class QuoteFragment extends Fragment {
	private TextView quoteText;
	private Spanned styledText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_quote, container, false);
		
		quoteText = (TextView) rootView.findViewById(R.id.quoteText);
		quoteText.setText(styledText);
		
		// determine if text needs to be shrunk to fit screen
		int textLength = quoteText.length();
		
		if (textLength < 340 && textLength >= 290)
			quoteText.setTextSize(18);
		else if (textLength < 390 && textLength >= 340)
			quoteText.setTextSize(16);
		else if (textLength >= 390)
			quoteText.setTextSize(14);
		else {
			// text size remains default value
		}
		
		setRetainInstance(true);
		
		return rootView;
	}
	
	// called by the initFragments() method in QuotesActivity before onCreateView is implicitly called
	public void setQuoteText(Spanned formattedQuote) {
		styledText = formattedQuote;
	}
}
