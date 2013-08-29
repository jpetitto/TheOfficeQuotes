package com.johnpetitto.theofficequotes;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

/*
 * Quote.java holds the specific information that is tied to each 
 * quote. Quote objects are constructed in the parseQuotes() method 
 * of the ParseXmlQuotes utility class and used through out many of
 * the classes in the project, especially the QuotesActivity class.
 * 
 * The class implements Parcelable since Quote objects are shared
 * via intents and performance is better with Parcels than it would
 * be with traditional serialization.
 */

public class Quote implements Parcelable {
	private String quote;
	private String episode;
	private String season;
	private String airdate;
	private String id;
	
	public Quote(String quote, String episode, String season, String airdate, String id) {
		this.quote = quote;
		this.episode = episode;
		this.season = season;
		this.airdate = airdate;
		this.id = id;
	}
	
	public Quote(Parcel in) {
		quote = in.readString();
		episode = in.readString();
		season = in.readString();
		airdate = in.readString();
		id = in.readString();
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(quote);
		dest.writeString(episode);
		dest.writeString(season);
		dest.writeString(airdate);
		dest.writeString(id);
	}
	
	public static final Parcelable.Creator<Quote> CREATOR = new Parcelable.Creator<Quote>() {
		public Quote createFromParcel(Parcel in) {
			return new Quote(in);
		}
		
		public Quote[] newArray(int size) {
			return new Quote[size];
		}
	};
	
	public String getQuote() {
		return quote;
	}
	
	public String getEpisode() {
		return episode;
	}
	
	public String getSeason() {
		return season;
	}
	
	public String getAirdate() {
		return airdate;
	}
	
	public String getId() {
		return id;
	}
	
	public Spanned getFormattedQuote() {
		return Html.fromHtml(quote);
	}
}
