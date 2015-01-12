package com.serwylo.emergencies.views;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.serwylo.emergencies.*;
import com.serwylo.emergencies.data.radiostations.*;

import java.util.*;

public class EmergencyRadioFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_emergency_radio_details, null );
		final ListView list = (ListView)view.findViewById( android.R.id.list );

		new RadioStationLoader( getActivity() ) {

			@Override
            public void onPostExecute( List<RadioStation> stations ) {
				list.setAdapter( new RadioStationAdapter( getActivity(), stations ) );
			}

		}.execute();

		return view;
    }

}
