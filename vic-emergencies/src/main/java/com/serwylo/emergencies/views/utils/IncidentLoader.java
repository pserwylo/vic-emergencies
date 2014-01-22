package com.serwylo.emergencies.views.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.serwylo.emergencies.data.Incident;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class IncidentLoader extends AsyncTask<Void, Void, List<Incident>> {

	private final String FEED_URL = "http://www.emergency.vic.gov.au/feed.json";

	private final Context context;

	public IncidentLoader( Context context ) {
		this.context = context;
	}

	private static void closeQuietly( Closeable stream ) {
		if (stream != null) {
			try {
				stream.close();
			} catch ( Exception e ) {}
		}
	}

	private String downloadJson() {

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
			closeQuietly( input );
			closeQuietly( output );
		}

		return json;

	}

	private static void copy(InputStream input, OutputStream output) throws IOException {

		final int BUFFER_SIZE = 4096;
		byte[] buffer = new byte[ BUFFER_SIZE ];
		while ( true ) {
			int count = input.read( buffer );
			if ( count == -1 ) {
				break;
			}
			output.write( buffer, 0, count );
		}
		output.flush();

	}

	private static class Cache {

		private static final long TIME_TO_KEEP = 1000 * 60 * 5; // 5 minutes
		private static final String PATH = "www.emergency.vic.gov.au-feed.json";

		private final Context context;
		private final File filePath;

		public Cache( Context context ) {
			this.context = context;
			filePath = new File( context.getFilesDir().getAbsolutePath() + "/" + PATH );
		}

		public boolean isCached() {
			return filePath.exists();
		}

		public boolean isStale() {
			long sinceModified = System.currentTimeMillis() - filePath.lastModified();
			return sinceModified > TIME_TO_KEEP;
		}

		public String load() {

			String json = null;
			FileInputStream input = null;
			ByteArrayOutputStream output = null;

			try {

				input = new FileInputStream( filePath );
				output = new ByteArrayOutputStream();
				copy(input, output);
				json = output.toString();

			} catch ( IOException e ) {
				Log.e("Incidents", e.getMessage());
			} finally {
				closeQuietly( input );
				closeQuietly( output );
			}

			return json;

		}

		public void save( String json ) {

			try {
				FileWriter writer = new FileWriter( filePath );
				writer.write( json );
				closeQuietly( writer );
			} catch ( IOException e ) {
				Log.e( "Incidents", e.getMessage() );
			}

		}
	}

	@Override
	protected List<Incident> doInBackground( Void... params ) {

		Cache cache = new Cache( context );

		String json = null;

		if ( cache.isCached() && !cache.isStale() ) {
			Log.i("Incidents", "Loading list of emergencies from cache." );
			json = cache.load();
		} else {
			if ( !cache.isCached() ) {
				Log.i( "Incidents", "Downloading list of emergencies for first time." );
			} else {
				Log.i( "Incidents", "Downloading list of emergencies again because the old list is too old." );
			}
			json = downloadJson();
			cache.save( json );
		}

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

			Log.i( "Incidents", "Loaded " + incidents.size() + " incidents from JSON data." );
			return incidents;

		} catch ( JSONException e ) {
			Log.e( "Incidents", "Error parsing JSON: " + e.getMessage() );
		}

		return new ArrayList<Incident>( 0 );

	}
}
