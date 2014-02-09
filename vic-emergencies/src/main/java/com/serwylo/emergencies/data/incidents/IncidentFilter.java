package com.serwylo.emergencies.data.incidents;

import java.util.*;

public abstract class IncidentFilter {

	public abstract boolean exclude( Incident incident );

	public static void filter( Collection<Incident> incidents, IncidentFilter filter ) {
		Iterator<Incident> iterator = incidents.iterator();
		while ( iterator.hasNext() ) {
			if ( filter.exclude( iterator.next() ) ) {
				iterator.remove();
			}
		}
	}

}
