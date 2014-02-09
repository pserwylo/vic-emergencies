package com.serwylo.emergencies.views;

import android.content.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.serwylo.emergencies.data.incidents.*;
import com.serwylo.emergencies.data.radiostations.*;

import java.util.*;

public class RadioStationAdapter extends ArrayAdapter<RadioStation> {

	public RadioStationAdapter( Context context, List<RadioStation> stations ) {
		super( context, android.R.layout.simple_list_item_2, stations );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( android.R.layout.simple_list_item_2, null );
		}

		TextView text1 = (TextView)convertView.findViewById( android.R.id.text1 );
		TextView text2 = (TextView)convertView.findViewById( android.R.id.text2 );

		RadioStation station = getItem( position );

		text1.setText( station.location );
		text2.setText( station.stations.get( 0 ) );

		return convertView;
	}
}
