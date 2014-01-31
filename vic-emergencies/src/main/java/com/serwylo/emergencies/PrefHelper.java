package com.serwylo.emergencies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.*;

import java.util.*;

public class PrefHelper implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String PREF_SHOW_NSW = "showNsw";
	private static final String PREF_SHOW_SA = "showSa";

	private static PrefHelper instance;

	public static void setup( Context context ) {
		if ( instance == null ) {
			instance = new PrefHelper( context.getApplicationContext() );
		}
	}

	public static PrefHelper get() {
		return instance;
	}

	private SharedPreferences preferences;

	private final ListenerManager stateFilterListener = new ListenerManager( PREF_SHOW_NSW, PREF_SHOW_SA );
	private final List<ListenerManager> listeners;

	private PrefHelper( Context context ) {
		preferences = PreferenceManager.getDefaultSharedPreferences( context.getApplicationContext() );
		preferences.registerOnSharedPreferenceChangeListener( this );
		listeners = new ArrayList<ListenerManager>( 1 );
		listeners.add( stateFilterListener );
	}

	public boolean showNswIncidents() {
		return preferences.getBoolean( PREF_SHOW_NSW, false );
	}

	public boolean showSaIncidents() {
		return preferences.getBoolean( PREF_SHOW_SA, false );
	}

	public void registerStateFilterListener( ChangeListener listener ) {
		stateFilterListener.register( listener );
	}

	public void unregisterStateFilterListener( ChangeListener listener ) {
		stateFilterListener.unregister( listener );
	}

	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
		for ( ListenerManager listener : listeners ) {
			if ( listener.listensTo( key ) ) {
				listener.onChange();
				break;
			}
		}
	}

	public static interface ChangeListener {
		public void onPreferenceChanged();
	}

	private static class ListenerManager {

		private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
		private List<String> preferenceNames;

		public ListenerManager( String ... preferences ) {
			preferenceNames = new ArrayList<String>( preferences.length );
			Collections.addAll( preferenceNames, preferences );
		}

		public void onChange() {
			for ( ChangeListener listener : listeners ) {
				listener.onPreferenceChanged();
			}
		}

		public boolean listensTo( String name ) {
			for ( String preferenceName : preferenceNames ) {
				if ( preferenceName.equals( name ) ) {
					return true;
				}
			}
			return false;
		}

		public void register( ChangeListener listener ) {
			if ( !listeners.contains( listener ) ) {
				listeners.add( listener );
			}
		}

		public void unregister( ChangeListener listener ) {
			if ( listeners.contains( listener ) ) {
				listeners.remove( listener );
			}
		}

	}

}
