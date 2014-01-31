package com.serwylo.emergencies.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SeverityComparator implements Comparator<Incident> {

	private static Map<String, Integer> severity = new HashMap<String, Integer>(5);

	private static final int UNKNOWN = 2;

	static {
		severity.put( "warningEvacuation", 6 );
		severity.put( "warningWatchAct", 5 );
		severity.put( "warningAdvice", 4 );
		severity.put( "fireActive", 3 );
		severity.put( "plannedBurn", 1 );
		severity.put( "fire", 0 );
	}

	private int getSeverity( Incident incident ) {
		if ( severity.containsKey( incident.getIcon() ) ) {
			return severity.get( incident.getIcon() );
		} else {
			return UNKNOWN;
		}
	}

	@Override
	public int compare( Incident lhs, Incident rhs ) {

		int lhsSeverity = getSeverity( lhs );
		int rhsSeverity = getSeverity( rhs );

		if ( lhsSeverity < rhsSeverity ) {
			return -1;
		} else if ( rhsSeverity < lhsSeverity ) {
			return 1;
		} else {
			return 0;
		}
	}
}
