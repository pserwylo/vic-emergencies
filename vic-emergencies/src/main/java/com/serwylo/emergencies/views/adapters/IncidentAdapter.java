package com.serwylo.emergencies.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.serwylo.emergencies.R;
import com.serwylo.emergencies.data.Incident;
import com.serwylo.emergencies.data.Location;
import com.serwylo.emergencies.views.IconFactory;

import java.util.List;

public class IncidentAdapter extends ArrayAdapter<Incident> {

	private LayoutInflater inflater = null;

	public IncidentAdapter( Context context, List<Incident> objects ) {
		super( context, 0, objects );
		inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}

	@Override
	public View getView( int position, View view, ViewGroup parent ) {
		if ( view == null ) {
			view = inflater.inflate( R.layout.item_incident, null );
		}

		Incident incident = getItem(position);

		ImageView icon = (ImageView)view.findViewById( R.id.image_icon );
		icon.setImageDrawable( IconFactory.instance( getContext() ).getIcon( incident ) );

		TextView name = (TextView)view.findViewById( R.id.text_name );
		name.setText( incident.getName() );

		TextView status = (TextView)view.findViewById( R.id.text_status );
		status.setText( incident.getStatus() );

		TextView locations = (TextView)view.findViewById( R.id.text_locations );
		StringBuilder sbLocations = new StringBuilder();
		if ( incident.getLocations().size() > 0 ) {
			for ( int i = 0; i < incident.getLocations().size(); i ++ ) {
				Location location = incident.getLocations().get( i );
				if ( i != 0 ) {
					sbLocations.append(", ");
				}
				sbLocations.append( location.getName() );
			}
		} else {
			sbLocations.append( "Unknown" );
		}
		locations.setText( sbLocations.toString() );

		TextView resourceCount = (TextView)view.findViewById( R.id.text_resource_count );
		if ( incident.isIncident() ) {
			resourceCount.setVisibility( View.VISIBLE );
			String s = incident.getResourceCount() == 1 ? "" : "s";
			resourceCount.setText( incident.getResourceCount() + " vehicle" + s + " attending" );
		} else {
			resourceCount.setVisibility( View.GONE );
		}

		return view;
	}

}
