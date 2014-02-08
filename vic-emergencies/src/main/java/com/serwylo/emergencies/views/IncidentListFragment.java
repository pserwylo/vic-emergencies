package com.serwylo.emergencies.views;

import android.app.*;
import android.os.*;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;
import com.serwylo.emergencies.*;
import com.serwylo.emergencies.data.*;
import com.serwylo.emergencies.views.adapters.*;

import java.util.*;

public class IncidentListFragment extends ListFragment implements AdapterView.OnItemClickListener {

	private IncidentMapFragment mapFragment = null;
	private List<Incident> incidents = null;
	private IncidentAdapter adapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated( savedInstanceState );
		setHasOptionsMenu( true );
		mapFragment = (IncidentMapFragment)getActivity().getSupportFragmentManager().findFragmentById( R.id.incident_map );
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
		if ( adapter != null ) {
			adapter.clear();
			addIncidentsToAdapter( this.incidents );
		}
    }

	private void addIncidentsToAdapter( List<Incident> incidentsToAdd ) {
		for ( Incident incident : incidentsToAdd ) {
			adapter.add( incident );
		}
	}

	@Override
	public void onAttach( Activity activity ) {
		super.onAttach( activity );
		adapter = new IncidentAdapter( activity.getApplicationContext(), new ArrayList<Incident>( 0 ) );
		if ( incidents != null ) {
			addIncidentsToAdapter( incidents );
		}
		setListAdapter( adapter );
	}
}
