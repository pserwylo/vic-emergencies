package com.serwylo.emergencies.data.incidents;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Incident {

	public static final String TYPE_WARNING = "Warning";

	public static final String STATE_VIC    = "VIC";
	public static final String STATE_NSW    = "NSW";
	public static final String STATE_SA     = "SA";

	private long id;
    private String type;
    private String name;
    private String status;
    private String icon;
    private String state;
    private String agencyShort;
    private String agencyUrl;
    private String htmlFurther;
    private Date createdTime;
    private Date updatedTime;
    private int severity;
    private String sizeHa;
    private int resourceCount;
    private String title;
    private String size;
    private String agencyArea;
    private Color colour;

	private List<Location> locations = new ArrayList<Location>();

	public long getId() { return id; }
	public String getName() { return name; }
	public String getStatus() { return status; }
	public String getType() { return type; }
	public String getIcon() { return icon; }
	public String getState() { return state; }
	public String getAgencyShort() { return agencyShort; }
	public String getAgencyUrl() { return agencyUrl; }
	public String getHtmlFurther() { return htmlFurther; }
	public Date getCreatedTime() { return createdTime; }
	public Date getUpdatedTime() { return updatedTime; }
	public int getSeverity() { return severity; }

	public String getSizeHa() { return sizeHa; }
	public int getResourceCount() { return resourceCount; }
	public String getTitle() { return title; }
	public String getSize() { return size; }
	public String getAgencyArea() { return agencyArea; }

	public List<Location> getLocations() { return locations; }

	public Incident(JSONObject json) throws JSONException {

		id = json.getLong( "id" );
		name = json.has( "name" ) ? json.getString( "name" ) : null;
		status = json.has( "status" ) ? json.getString( "status" ) : null;
		createdTime = json.has( "createdTime" ) ? new Date( json.getLong( "createdTime" ) ) : null;
		updatedTime = json.has( "updatedTime" ) ? new Date( json.getLong( "updatedTime" ) ) : null;
		severity = json.has( "severity" ) ? json.getInt( "severity" ) : -1;
		type = json.has( "type" ) ? json.getString( "type" ) : null;
		icon = json.has( "icon" ) ? json.getString( "icon" ) : null;
		state = json.has( "state" ) ? json.getString( "state" ) : null;
		agencyShort = json.has( "agencyShort" ) ? json.getString( "agencyShort" ) : null;
		agencyUrl = json.has( "agencyUrl" ) ? json.getString( "agencyUrl" ) : null;
		htmlFurther = json.has( "htmlFurther" ) ? json.getString( "htmlFurther" ) : null;
		sizeHa = json.has( "sizeHa" ) ? json.getString( "sizeHa" ) : null;
		resourceCount = json.has( "resourceCount" ) ? json.getInt( "resourceCount" ) : -1;
		title = json.has( "title" ) ? json.getString( "title" ) : null;
		size = json.has( "size" ) ? json.getString( "size" ) : null;
		agencyArea = json.has( "agencyArea" ) ? json.getString( "agencyArea" ) : null;

		if ( json.has( "geo" ) ) {
			JSONObject geo = json.getJSONObject( "geo" );
			if ( geo.has( "points" ) ) {
				JSONArray points = geo.getJSONArray( "points" );
				for ( int i = 0; i < points.length(); i ++ ) {
					locations.add( new Location( points.getJSONObject( i ) ) );
				}

				String boundingBox = geo.has( "bbox" ) ? geo.getString( "bbox" ) : null;
			}
		}
	}

	public boolean isIncident() {
		return type.equals( "Incident" );
	}

	public String getLocationsString() {
		StringBuilder sb = new StringBuilder();
		if ( locations != null && locations.size() > 0 ) {
			for ( int i = 0; i < locations.size(); i ++ ) {
				if ( i > 0 ) {
					sb.append( ", " );
				}
				sb.append( locations.get( i ).getName() );
			}
		} else {
			sb.append( "Unknown location" );
		}
		return sb.toString();
	}

	public String getDetailsUrl() {
		if ( TYPE_WARNING.equals( type ) ) {
			return "http://www.emergency.vic.gov.au/warnings/" + id + "_bare.html";
		} else {
			return null;
		}
	}
}
