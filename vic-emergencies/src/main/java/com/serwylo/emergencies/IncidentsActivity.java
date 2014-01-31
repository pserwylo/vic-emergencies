package com.serwylo.emergencies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import com.serwylo.emergencies.data.Incident;
import com.serwylo.emergencies.data.SeverityComparator;
import com.serwylo.emergencies.views.IncidentListFragment;
import com.serwylo.emergencies.views.IncidentMapFragment;
import com.serwylo.emergencies.views.utils.IncidentLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IncidentsActivity extends ActionBarActivity {

	private IncidentMapFragment mapFragment = null;
	private IncidentListFragment listFragment = null;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.incidents );

		Fragment mapFragment  = getSupportFragmentManager().findFragmentById( R.id.incident_map );
		Fragment listFragment = getSupportFragmentManager().findFragmentById( R.id.incident_list );

		if (mapFragment != null) {
			this.mapFragment  = (IncidentMapFragment)mapFragment;
		}
		if (listFragment != null) {
			this.listFragment = (IncidentListFragment)listFragment;
		}

		boolean dualView = this.listFragment != null && this.mapFragment != null;

		if (!dualView) {
			ActionBar actionBar = getSupportActionBar();
			actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

            TabListener<IncidentListFragment> listTab = new TabListener<IncidentListFragment>( this, "list", IncidentListFragment.class );
            this.listFragment = listTab.getFragment();

			actionBar.addTab(
				actionBar.newTab()
					.setText( "List" )
					.setTabListener( listTab )
			);

            TabListener<IncidentMapFragment> mapTab = new TabListener<IncidentMapFragment>( this, "map", IncidentMapFragment.class );
            this.mapFragment = mapTab.getFragment();

			actionBar.addTab(
                actionBar.newTab()
                    .setText("Map")
                    .setTabListener( mapTab )
			);
		}

	}

    private void refreshCache() {

        if ( mapFragment != null ) {
            mapFragment.setIncidentList(new ArrayList<Incident>(0));
        }

        if ( listFragment != null ) {
            listFragment.setIncidentList(new ArrayList<Incident>(0));
        }

        new IncidentLoader( this, true ) {
            @Override
            public void onPostExecute( List<Incident> result ) {
                if (mapFragment != null) {
                    mapFragment.setIncidentList( result );
                }
                if (listFragment != null) {
                    listFragment.setIncidentList( result );
                }
                Toast.makeText(
                    IncidentsActivity.this,
                    "Refreshed list of incidents from www.emergency.vic.gov.au",
                    Toast.LENGTH_SHORT ).show();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.incident_list_menu, menu);
		MenuItemCompat.setShowAsAction(menu.findItem( R.id.menu_settings ), MenuItemCompat.SHOW_AS_ACTION_ALWAYS );
        MenuItemCompat.setShowAsAction( menu.findItem( R.id.menu_refresh ), MenuItemCompat.SHOW_AS_ACTION_IF_ROOM );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshCache();
                return true;
            case R.id.menu_settings:
                Intent intent = new Intent( this, SettingsActivity.class );
				startActivity( intent );
                return true;
        }
        return false;
    }
}

class TabListener<T extends Fragment> implements ActionBar.TabListener {

	private T fragment;
	private final String tag;
    private boolean hasBeenAdded = false;

	public TabListener(Activity activity, String tag, Class<T> clazz) {
		this.tag = tag;
        this.fragment = (T)Fragment.instantiate( activity, clazz.getName() );
	}

    public T getFragment() {
        return fragment;
    }

	@Override
	public void onTabSelected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
		if ( !hasBeenAdded ) {
			fragmentTransaction.add( android.R.id.content, fragment, tag );
            hasBeenAdded = true;
		} else {
			fragmentTransaction.attach( fragment );
		}
	}

	@Override
	public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
        fragmentTransaction.detach( fragment );
	}

	@Override
	public void onTabReselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {

	}
}
