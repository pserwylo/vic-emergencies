package com.serwylo.emergencies.views;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.serwylo.emergencies.*;
import com.serwylo.emergencies.data.*;
import com.serwylo.emergencies.views.adapters.*;

import java.util.*;

public class IncidentListFragment extends ListFragment implements AdapterView.OnItemClickListener {

	private IncidentMapFragment mapFragment = null;
	private List<Incident> incidents = null;

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
        setListAdapter( new IncidentAdapter( getActivity(), incidents ) );
    }

}
