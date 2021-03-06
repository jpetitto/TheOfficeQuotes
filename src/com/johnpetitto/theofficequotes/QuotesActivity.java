package com.johnpetitto.theofficequotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/*
 * QuotesActivity.java is responsible for displaying the quotes sent
 * from the MainMenuActivity (via intent) in a ViewPager. By maintaining
 * a list of viewable quotes, it provides functionality for adding to a
 * list of favorites, sharing the quote via other apps, and instantiating
 * a dialog box containing additional information about each quote. An
 * index of each quote is also displayed by the class.
 */

public class QuotesActivity extends ActionBarActivity {
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private List<Fragment> quoteFragments;
	private TextView quoteIndex;
	private ArrayList<Quote> quotes;
	private Toast toast;
    private ShareActionProvider mShareActionProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes);
		
		// check if instance state was previously saved
		if (savedInstanceState != null) {
			quotes = savedInstanceState.getParcelableArrayList("quotes");
		} else {
			quotes = getIntent().getParcelableArrayListExtra("quotes");
		}
		
		// instantiate and configure ViewPager
		mPager = (ViewPager) findViewById(R.id.pager);
		initFragments();
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new QuotePageChangeListener());
		
		// display index of current Quote
		quoteIndex = (TextView) findViewById(R.id.quoteIndex);
		quoteIndex.setText((mPager.getCurrentItem() + 1) + "/" + quoteFragments.size());
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelableArrayList("quotes", quotes);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        // modify share action item to use default share intent
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getShareIntent());

        return super.onCreateOptionsMenu(menu);
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareText = getCurrentQuote().getFormattedQuote().toString();
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
                viewInfo(null);
                return true;
            case R.id.action_favorite:
                addFavorite(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	// constructs each QuoteFragment to be added to the ViewPager
	private void initFragments() {
		quoteFragments = new Vector<Fragment>();
		
		for (int i = 0; i < quotes.size(); i++) {
			QuoteFragment qf = new QuoteFragment();
			qf.setQuoteText(quotes.get(i).getFormattedQuote());
			quoteFragments.add(qf);
		}
		
		mPagerAdapter = new QuoteSliderAdapter(getSupportFragmentManager(), quoteFragments);
	}
	
	// nested class is responsible for ViewPager functionality
	private class QuoteSliderAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> fragments;
		
		public QuoteSliderAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}
		
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
		
		@Override
		public int getCount() {
			return fragments.size();
		}
	}
	
	// nested class is responsible for notifying when a new quote is displayed
	private class QuotePageChangeListener extends SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			// update quote index display
			quoteIndex.setText((position + 1) + "/" + quoteFragments.size());
            // update the text to be shared
            mShareActionProvider.setShareIntent(getShareIntent());
		}
	}
	
	// adds current quote to a list of favorites (carried out by ParseXmlQuotes class)
	public void addFavorite(View view) {
        String toastText;

        if (getCurrentQuote().isFavorite()) {
            // remove quote from favorites
            getCurrentQuote().setFavorite(false);
            toastText = "Removed from Favorites";
        } else {
            // add quote to favorites
            getCurrentQuote().setFavorite(true);
            toastText = "Added to Favorites";
        }

        try {
            ParseXmlQuotes.updateFavorites(getCurrentQuote(), getApplicationContext());
        } catch (IOException ex) {
            toastText = "Error: could not add/remove to Favorites";
        }

        toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	// enables user to choose from a list of available programs to share current quote
	public void shareQuote(View view) {
		String shareText = getCurrentQuote().getFormattedQuote().toString();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, shareText);
		startActivity(Intent.createChooser(intent, "Share Quote"));
	}
	
	// instantiates a custom dialog box to display additional info about the current quote
	public void viewInfo(View view) {
		InfoDialog dialog = new InfoDialog();
		
		Bundle bundle = new Bundle();
		bundle.putString("season", getCurrentQuote().getSeason());
		bundle.putString("episode", getCurrentQuote().getEpisode());
		bundle.putString("airdate", getCurrentQuote().getAirdate());
		dialog.setArguments(bundle);
		
		dialog.show(getSupportFragmentManager(), "InfoDialog");
	}
	
	// helper method
	private Quote getCurrentQuote() {
		return quotes.get(mPager.getCurrentItem());
	}
}
