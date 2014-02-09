package com.serwylo.emergencies.views;

import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.serwylo.emergencies.R;

public class EmergencyPhoneFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_emergency_phone_details, null );
		ImageButton dial000 = (ImageButton) view.findViewById( R.id.btn_call_000 );
		ImageButton dialBushfire = (ImageButton) view.findViewById( R.id.btn_call_bushfire_hotline );

		dial000.setOnClickListener( new Button.OnClickListener() {
			@Override
			public void onClick( View v ) {
				dial( "000" );
			}
		});

		dialBushfire.setOnClickListener( new Button.OnClickListener() {
			@Override
			public void onClick( View v ) {
				dial( "1800 240 667" );
			}
		});

		return view;
    }

	private void dial( String number ) {
		Intent intent = new Intent( Intent.ACTION_DIAL, Uri.parse( "tel:" + number ) );
		startActivity( intent );
	}

}
