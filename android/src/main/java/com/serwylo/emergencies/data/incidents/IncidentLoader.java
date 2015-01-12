package com.serwylo.emergencies.data.incidents;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.serwylo.emergencies.PrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public abstract class IncidentLoader extends AsyncTask<Void, Void, List<Incident>> {

	private final String FEED_URL = "http://www.emergency.vic.gov.au/feed.json";

	private final Context context;

	private static List<Incident> cachedIncidents = null;

	public IncidentLoader( Context context ) {
		this( context, false );
	}

    public IncidentLoader( Context context, boolean clearCache ) {
        this.context = context;
		if ( clearCache ) {
			clearCache();
		}
    }

	private void clearCache() {
		Log.i( "Incidents", "Clearing cached list of incidents due to manual refresh." );
		cachedIncidents = null;
		IncidentCache.get( context ).clear();
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

	@Override
	protected List<Incident> doInBackground( Void... params ) {

		IncidentCache cache = IncidentCache.get( context );
		List<Incident> incidents;

		if ( !cache.isStale() && cachedIncidents != null ) {

			Log.i( "Incidents", "Loading list of emergencies from cache (already parsed JSON)." );
			incidents = cachedIncidents;

		} else {

			String json;
			if ( cache.isCached() && !cache.isStale() ) {

				Log.i( "Incidents", "Parsing cached JSON file to get incidents." );
				json = cache.load();

			} else {

				if ( !cache.isCached() ) {
					Log.i( "Incidents", "Downloading list of emergencies for first time." );
				} else {
					Log.i( "Incidents", "Downloading list of emergencies again because the old list is too old." );
				}

				json = downloadJson();
				if (json == null) {
					return null;
				}
				cache.save( json );
			}

			incidents = jsonToIncidents( json );
		}

		cachedIncidents = new ArrayList<Incident>( incidents );

		filterIncidents( incidents );
		sortIncidents( incidents );

		return incidents;

	}

	private static List<Incident> jsonToIncidents( String json ) {
		List<Incident> incidents;
		if ( json == null || json.trim().length() == 0 ) {
			Log.e("Incidents", "Could not load json." );
			return new ArrayList<Incident>( 0 );
		} else {
            try {
                JSONObject object = new JSONObject( json );
                JSONArray events = object.getJSONArray( "events" );
                incidents = new ArrayList<Incident>( events.length() );

                for (int i = 0; i < events.length(); i ++) {
                    Incident incident = new Incident(events.getJSONObject( i ) );
                    incidents.add(incident);
                }

                Log.i( "Incidents", "Loaded " + incidents.size() + " incidents from JSON data." );
            } catch ( JSONException e ) {
                Log.e( "Incidents", "Error parsing JSON: " + e.getMessage() );
                incidents = new ArrayList<Incident>( 0 );
            }
        }
		return incidents;
	}

	private static void filterIncidents( List<Incident> incidents ) {
		List<String> validStates = new ArrayList<String>( 3 );
		validStates.add( Incident.STATE_VIC );
		if ( PrefHelper.get().showNswIncidents() ) {
			validStates.add( Incident.STATE_NSW );
		}
		if ( PrefHelper.get().showSaIncidents() ) {
			validStates.add( Incident.STATE_SA );
		}
		IncidentFilter.filter( incidents, new StateFilter( validStates ) );
	}

	private static void sortIncidents( List<Incident> incidents ) {
		Comparator<Incident> comparator = null;
		if ( PrefHelper.get().sortBy().equals( PrefHelper.PREF_SORT_IMPORTANCE ) ) {
			comparator = new ImportanceComparator();
		} else if ( PrefHelper.get().sortBy().equals( PrefHelper.PREF_SORT_RECENT ) ) {
			comparator = new RecentComparator();
		}

		if ( comparator != null ) {
			Collections.sort( incidents, comparator );
		}
	}

    private static class IncidentCache {

        private static final long TIME_TO_KEEP = 1000 * 60 * 5; // 5 minutes
        private static final String PATH = "www.emergency.vic.gov.au-feed.json";

        private final File filePath;

		private static IncidentCache instance = null;

		public static IncidentCache get( Context context ) {
			if ( instance == null ) {
				instance = new IncidentCache( context );
			}
			return instance;
		}

        private IncidentCache( Context context ) {
            filePath = new File( context.getFilesDir().getAbsolutePath() + "/" + PATH );
        }

		public void clear() {
			filePath.delete();
		}

        public boolean isCached() {
            return filePath.exists();
        }

        public boolean isStale() {
			if ( !isCached() ) {
				return true;
			} else {
				long sinceModified = System.currentTimeMillis() - filePath.lastModified();
				return sinceModified > TIME_TO_KEEP;
			}
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

			if ( json == null ) {
				Log.i( "Incidents", "Not caching incident list, because we didn't manage to download one." );
			} else {
				try {
					FileWriter writer = new FileWriter( filePath );
					writer.write( json );
					closeQuietly( writer );
				} catch ( IOException e ) {
					Log.e( "Incidents", e.getMessage() );
				}
			}

        }
    }

}