package com.serwylo.emergencies.views.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.serwylo.emergencies.data.Incident;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class IncidentLoader extends AsyncTask<Void, Void, List<Incident>> {

	private final String FEED_URL = "http://www.emergency.vic.gov.au/feed.json";

	private String loadJson() {

		String json = null;
		InputStream input = null;
		ByteArrayOutputStream output = null;

		try {

			URL url = new URL( FEED_URL );
			input = url.openStream();
			output = new ByteArrayOutputStream();
			copy(input, output);
			json = output.toString();

		} catch ( MalformedURLException e ) {
			Log.e("Incidents", e.getMessage());
		} catch ( IOException e ) {
			Log.e("Incidents", e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch ( Exception e ) {}
			}
			if (output != null) {
				try {
					output.close();
				} catch ( Exception e ) {}
			}
		}

		return json;

	}

	public void copy(InputStream input, OutputStream output)
			throws IOException {

		final int BUFFER_SIZE = 4096;
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int count = input.read(buffer);
			if (count == -1) {
				break;
			}
			output.write(buffer, 0, count);
		}
		output.flush();

	}

	@Override
	protected List<Incident> doInBackground( Void... params ) {

		String json = loadJson();
		List<Incident> incidents = null;

		if ( json == null || json.trim().length() == 0 ) {
			Log.e("Incidents", "Could not load json." );
			return new ArrayList<Incident>( 0 );
		}

		try {

			JSONObject object = new JSONObject( json );
			JSONArray events = object.getJSONArray( "events" );
			incidents = new ArrayList<Incident>( events.length() );
			for (int i = 0; i < events.length(); i ++) {
				Incident incident = new Incident(events.getJSONObject( i ) );
				incidents.add(incident);
			}

			return incidents;

		} catch ( JSONException e ) {
			Log.e( "Incidents", "Error parsing JSON: " + e.getMessage() );
		}

		return new ArrayList<Incident>( 0 );

	}
}
