package com.serwylo.emergencies.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;

import com.serwylo.emergencies.R;
import com.serwylo.emergencies.data.Incident;
import com.serwylo.emergencies.data.SeverityComparator;
import com.serwylo.emergencies.views.adapters.IncidentAdapter;
import com.serwylo.emergencies.views.utils.IncidentLoader;

import java.util.Collections;
import java.util.List;

public class IncidentListFragment extends ListFragment implements AdapterView.OnItemClickListener {

	private IncidentMapFragment mapFragment = null;
	private List<Incident> incidents = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );

		mapFragment = (IncidentMapFragment)getActivity().getSupportFragmentManager().findFragmentById( R.id.incident_map );

		try {
			new IncidentLoader( getActivity() ) {
				@Override
				public void onPostExecute( List<Incident> result ) {
					setIncidentList( result );
				}
			}.execute();
		} catch ( Exception e ) {
			Log.e( "Incidents", e.getMessage() );
		}

		getListView().setOnItemClickListener( this );
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		if ( mapFragment != null ) {
			mapFragment.centerMapOn( ( Incident ) parent.getItemAtPosition( position ) );
		}
		AdviceDialog.showAdviceIfAvailable( getActivity(), incidents.get( position ) );
	}

    public void setIncidentList( List<Incident> incidents ) {
        this.incidents = incidents;
        if ( incidents != null ) {
            Collections.sort( incidents, Collections.reverseOrder( new SeverityComparator() ) );
        }
        setListAdapter( new IncidentAdapter( getActivity(), incidents ) );
    }

}
