package com.serwylo.emergencies.data;

import android.util.*;

import java.util.*;

public class StateFilter extends IncidentFilter {

	private List<String> statesToInclude;

	public StateFilter( List<String> statesToInclude ) {
		this.statesToInclude = statesToInclude;
	}

	@Override
	public boolean exclude( Incident incident ) {
		boolean exclude = true;

		if ( incident.getState() == null || incident.getState().trim().length() == 0 ) {
			// I realise this may end up with incidents on the other side of the country, but it is probably more
			// important to put up with those false-positives than it is to miss out on a fire that happens to
			// be right next to you.
			exclude = false;
		} else {
			for ( String state : statesToInclude ) {
				if ( state.equals( incident.getState() ) ) {
					exclude = false;
					break;
				}
			}
		}
		return exclude;
	}
}
