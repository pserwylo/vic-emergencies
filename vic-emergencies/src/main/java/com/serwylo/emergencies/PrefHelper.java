package com.serwylo.emergencies;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class PrefHelper {

	private static final String FILE_NAME = "incident_preferences";

	private static final String PREF_SHOW_NSW = "showNsw";
	private static final String PREF_SHOW_SA = "showSa";

	private SharedPreferences preferences;

	public PrefHelper( Context context ) {
		preferences = context.getSharedPreferences( FILE_NAME, Context.MODE_PRIVATE );
	}

	public boolean showNswIncidents() {
		return preferences.getBoolean( PREF_SHOW_NSW, false );
	}

	public boolean showSaIncidents() {
		return preferences.getBoolean( PREF_SHOW_SA, false );
	}

	public static interface ChangeListener {
		public void onPreferenceChanged();
	}

}
