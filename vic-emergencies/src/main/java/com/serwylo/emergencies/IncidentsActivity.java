package com.serwylo.emergencies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.serwylo.emergencies.views.IncidentListFragment;
import com.serwylo.emergencies.views.IncidentMapFragment;

public class IncidentsActivity extends ActionBarActivity {

	private IncidentMapFragment mapFragment = null;
	private IncidentListFragment listFragment = null;

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.incidents_menu, menu );
		return super.onCreateOptionsMenu( menu );
	}

	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( item.getItemId() == R.id.menu_information_services ) {
			Intent intent = new Intent( this, AlternateInfoActivity.class );
			startActivity( intent );
			return true;
		}
		return false;
	}

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

			actionBar.addTab(
				actionBar.newTab()
					.setText( "List" )
					.setTabListener( new TabListener<IncidentListFragment>( this, "list", IncidentListFragment.class ) )
			);

			actionBar.addTab(
					actionBar.newTab()
							.setText( "Map" )
							.setTabListener( new TabListener<IncidentMapFragment>( this, "map", IncidentMapFragment.class ) )
			);
		}

	}

}

class TabListener<T extends Fragment> implements ActionBar.TabListener {

	private T fragment;
	private final Activity activity;
	private final String tag;
	private final Class<T> clazz;

	public TabListener(Activity activity, String tag, Class<T> clazz) {
		this.activity = activity;
		this.tag = tag;
		this.clazz = clazz;
	}

	@Override
	public void onTabSelected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
		if ( fragment == null ) {
			fragment = (T)Fragment.instantiate( activity, clazz.getName() );
			fragmentTransaction.add( android.R.id.content, fragment, tag );
		} else {
			fragmentTransaction.attach( fragment );
		}
	}

	@Override
	public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
		if ( fragment != null ) {
			fragmentTransaction.detach( fragment );
		}
	}

	@Override
	public void onTabReselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {

	}
}
