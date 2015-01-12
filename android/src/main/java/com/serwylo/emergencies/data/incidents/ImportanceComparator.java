package com.serwylo.emergencies.data.incidents;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ImportanceComparator implements Comparator<Incident> {

	private static Map<String, Integer> severity = new HashMap<String, Integer>(5);

	private static final Comparator<Incident> TIE_BREAK_COMPARATOR = new RecentComparator();

	private static final int UNKNOWN = 4;

	static {
		severity.put( "warningEvacuation", 0 );
		severity.put( "warningWatchAct", 1 );
		severity.put( "warningAdvice", 2 );
		severity.put( "fireActive", 3 );
		severity.put( "plannedBurn", 5 );
		severity.put( "fire", 6 );
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
			return TIE_BREAK_COMPARATOR.compare( lhs, rhs );
		}
	}
}
