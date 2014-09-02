package com.johnpetitto.theofficequotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/*
 * InfoDialog.java is a custom dialog box for displaying
 * additional information about a Quote. Used by the
 * viewInfo() method found in the QuotesActivity class.
 */

public class InfoDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		String message = "Season: " + getArguments().getString("season");
		message += "\nEpisode: " + getArguments().getString("episode");
		message += "\nAirdate: " + getArguments().getString("airdate");
		
		builder.setTitle(R.string.action_about);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.info_return, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// dialog closes
			}
		});
		
		return builder.create();
	}
}
