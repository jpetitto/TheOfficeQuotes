package com.johnpetitto.theofficequotes;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * ParseXmlQuotes.java is a utility class used to carry out work
 * involving XML parsing and file I/O. The class is thus non-instantiable
 * and is used by two classes: MainMenuActivity (parseQuotes) and QuotesActivity
 * (addToFavorites). Check method comments for further details.
 */

public class ParseXmlQuotes {
	private final static String FILE_NAME = "quotes.xml";
	private final static String FAVORITES = "favorites";
	private final static byte[] NEW_LINE = "\n".getBytes();
	
	private ParseXmlQuotes() {
		// class is non-instantiable
	}
	
	// parses quotes.xml and adds quotes that match category to the list passed as a parameter
	public static void parseQuotes(String category, ArrayList<Quote> quotes, Context context)
			throws XmlPullParserException, IOException {
		
		// check that the list passed has been instantiated
		if (quotes == null)
			return;
		
		// create and configure parser
		XmlPullParser parser = Xml.newPullParser();
		InputStreamReader reader = new InputStreamReader(context.getAssets().open(FILE_NAME));
		parser.setInput(reader);
		
		// variables for holding the content of each quote
		String season = null;
		String episode = null;
		String airdate = null;
		String quote = null;
		String id = null;
        boolean isFavorite = false;
		
		// flags for signaling a specific type of category
		boolean matchesCharacter = false;
		boolean matchesSeason = false;
		boolean parseAllQuotes = false;
		
		// check if parseAllQuotes flag should be set
		if (category.equals("All"))
			parseAllQuotes = true;
		
		// trim category if season to match style of XML document
		if (category.contains("Season"))
			category = category.substring("Season".length() + 1);
		
		// gather list of favorites if necessary
		HashSet<String> favoriteIds = getFavoriteIds(context);
		
		// start parsing the XML document
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				// found a new quote
				if (parser.getName().equals("quote")) {
					id = parser.getAttributeValue(0); // grab ID attribute
					if (favoriteIds.contains(id))
                        isFavorite = true;
				}
				else if (parser.getName().equals("character")) {
					if (parser.nextText().equals(category))
						matchesCharacter = true;
				}
				else if (parser.getName().equals("season")) {
					season = parser.nextText();
					if (season.equals(category))
						matchesSeason = true;
				}
				else if (parser.getName().equals("episode"))
					episode = parser.nextText();
				else if (parser.getName().equals("airdate"))
					airdate = parser.nextText();
				else if (parser.getName().equals("text"))
					quote = parser.nextText();
				else {
					// skip tag
				}
			}
			if (eventType == XmlPullParser.END_TAG) {
				// finished parsing a quote
				if (parser.getName().equals("quote")) {
					// at least one flag needs to be set in order to be added
					if (matchesCharacter || matchesSeason || (isFavorite && category.equals("Favorites")) || parseAllQuotes)
						quotes.add(new Quote(quote, episode, season, airdate, id, isFavorite));
					
					// reset flags (except parseAllQuotes) for further parsing
					matchesCharacter = false;
					matchesSeason = false;
					isFavorite = false;
				}
			}
			
			eventType = parser.next(); // continue to next element
		}
		
		reader.close(); // finished parsing XML document
	}

    /*
	// writes (or removes) ID of quote to a file for future use in parsing
	// note: method is protected (default) since it is both static and returns a value
	static boolean addToFavorites(String id, Context context) throws IOException {
		ArrayList<String> favoriteIds;
		FileOutputStream fos;
		
		// call helper method to gather list of existing favorite quote IDs
		favoriteIds = getFavoriteIds(context);
		fos = context.openFileOutput(FAVORITES, Context.MODE_PRIVATE);
		
		// if no favorites exist, write current ID to file
		if (favoriteIds.isEmpty()) {
			fos.write(id.getBytes());
			fos.write(NEW_LINE);
			fos.close();
			return true;
		}
		
		// otherwise check if current ID is already a favorite
		int origSize = favoriteIds.size();
		if (favoriteIds.contains(id))
			favoriteIds.remove(favoriteIds.indexOf(id));
		else 
			favoriteIds.add(id);
		
		// rewrite to file with newly added/removed ID
		for (int i = 0; i < favoriteIds.size(); i++) {
			String readID = favoriteIds.get(i);
			fos.write(readID.getBytes());
			fos.write(NEW_LINE);
		}
		
		fos.close();
		
		return origSize < favoriteIds.size(); // determines if ID was added (true) or removed
	}
	*/

    // writes (or removes) ID of quote to a file for future use in parsing
    protected static void updateFavorites(Quote quote, Context context) throws IOException {
        // call helper method to gather set of existing favorite quote IDs
        HashSet<String> favoriteIds = getFavoriteIds(context);
        FileOutputStream fos = context.openFileOutput(FAVORITES, Context.MODE_PRIVATE);

        // add or remove quote ID from favorites
        if (quote.isFavorite()) {
            favoriteIds.add(quote.getId());
        } else {
            favoriteIds.remove(quote.getId());
        }

        // rewrite to file with newly added/removed ID
        for (String favorite : favoriteIds) {
            fos.write(favorite.getBytes());
            fos.write(NEW_LINE);
        }

        fos.close();
    }
	
	// helper method used by both public methods of the class in order to read favorite IDs from file
	private static HashSet<String> getFavoriteIds(Context context) {
		try {
			HashSet<String> favoriteIds = new HashSet<String>();
			FileInputStream fis = context.openFileInput(FAVORITES);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			
			String inputString = reader.readLine();
			while (inputString != null) {
				favoriteIds.add(inputString);
				inputString = reader.readLine();
			}
			
			fis.close();
			reader.close();
			
			return favoriteIds;
		} catch (IOException e) {
			return new HashSet<String>();
		}
	}
}
