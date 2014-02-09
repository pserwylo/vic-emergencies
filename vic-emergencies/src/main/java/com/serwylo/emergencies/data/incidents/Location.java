package com.serwylo.emergencies.data.incidents;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {

	private String name;
	private String type;
    private String locationInfo;
    private double latitude;
    private double longitude;
    private String localGovernmentArea;
    private String stateGovernmentRegion;

	public String getName() { return name; }
	public String getType() { return type; }
	public String getLocationInfo() { return locationInfo; }
	public double getLatitude() { return latitude; }
	public double getLongitude() { return longitude; }
	public String getLocalGovernmentArea() { return localGovernmentArea; }
	public String getStateGovernmentRegion() { return stateGovernmentRegion; }

	public Location( JSONObject json ) throws JSONException {
		name = json.has( "name" ) ? json.getString( "name" ) : null;
		type = json.has( "type" ) ? json.getString( "type" ) : null;
		locationInfo = json.has( "locationInfo" ) ? json.getString( "locationInfo" ) : null;
		latitude = json.has( "latitude" ) ? json.getDouble( "latitude" ) : 0.0;
		longitude = json.has( "longitude" ) ? json.getDouble( "longitude" ) : 0.0;
		localGovernmentArea = json.has( "localGovernmentArea" ) ? json.getString( "localGovernmentArea" ) : null;
		stateGovernmentRegion = json.has( "stateGovernmentRegion" ) ? json.getString( "stateGovernmentRegion" ) : null;
	}

}
