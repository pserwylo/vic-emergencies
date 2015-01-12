package com.serwylo.emergencies.data.radiostations;

import android.content.*;
import android.os.*;
import android.util.*;
import com.serwylo.emergencies.*;
import com.serwylo.emergencies.data.incidents.*;
import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

public abstract class RadioStationLoader extends AsyncTask<Void, Void, List<RadioStation>> {

	private final Context context;

	private static List<Incident> cachedIncidents = null;

	public RadioStationLoader( Context context ) {
		this.context = context.getApplicationContext();
	}

	private static void closeQuietly( Closeable stream ) {
		if (stream != null) {
			try {
				stream.close();
			} catch ( Exception e ) {}
		}
	}

	@Override
	protected List<RadioStation> doInBackground( Void... params ) {
		List<RadioStation> stationsInOrder = new ArrayList<RadioStation>();
		Map<String, RadioStation> stations = new HashMap<String, RadioStation>();
		try {
			InputStream input = context.getResources().openRawResource( R.raw.radio_stations );
			BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );
			String line = reader.readLine();
			while ( line != null ) {
				String[] lineParts = line .split( "\t" );
				String location = lineParts[ 0 ];
				String station = lineParts[ 1 ];

				RadioStation r;
				if (stations.containsKey( location ) ) {
					r = stations.get( location );
				} else {
					r = new RadioStation();
					r.location = location;
					stations.put( location, r );
					stationsInOrder.add( r );
				}
				r.stations.add( station );
				line = reader.readLine();
			}
		} catch ( IOException ioe ) {
			Log.e( "Incidents", "Error loading radio station list from CSV: " + ioe.getMessage() );
		}

		return stationsInOrder;

	}

}