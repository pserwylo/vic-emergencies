package com.serwylo.emergencies.data.incidents;

import java.util.*;

public class RecentComparator implements Comparator<Incident> {

	@Override
	public int compare( Incident lhs, Incident rhs ) {
		return lhs.getCreatedTime().compareTo( rhs.getCreatedTime() );
	}

}
