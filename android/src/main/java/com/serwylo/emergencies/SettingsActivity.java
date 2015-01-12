package com.serwylo.emergencies;

import android.annotation.TargetApi;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Utils.refreshTheme( this );
		super.onCreate( savedInstanceState );
		setupActionBar();
		addPreferencesFromResource( R.xml.pref_general );

    }

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
		updateSortBySummary();
		updateThemeSummary();
	}

	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
	}

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
		if ( key.equals( PrefHelper.PREF_SORT ) ) {
			updateSortBySummary();
		} else if ( key.equals( PrefHelper.PREF_THEME ) ) {
			updateThemeSummary();

			// Seem to need to restart the activity for the theme to apply...
			finish();
			startActivity( new Intent( this, getClass() ) );
		}
	}

	private void updateSortBySummary() {
		ListPreference preference = (ListPreference)findPreference( PrefHelper.PREF_SORT );
		if ( PrefHelper.get().sortBy().equals( PrefHelper.PREF_SORT_IMPORTANCE ) ) {
			preference.setSummary( "Most important incidents first" );
		} else if ( PrefHelper.get().sortBy().equals( PrefHelper.PREF_SORT_RECENT ) ) {
			preference.setSummary( "Most recent incidents first" );
		}
	}

	private void updateThemeSummary() {
		ListPreference preference = (ListPreference)findPreference( PrefHelper.PREF_THEME );
		if ( PrefHelper.get().theme().equals( PrefHelper.PREF_THEME_LIGHT ) ) {
			preference.setSummary( "Light theme (uses more battery)" );
		} else if ( PrefHelper.get().theme().equals( PrefHelper.PREF_THEME_DARK ) ) {
			preference.setSummary( "Dark theme (saves battery)" );
		}
	}

}
