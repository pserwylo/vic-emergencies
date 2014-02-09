package com.serwylo.emergencies.views;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.serwylo.emergencies.R;
import com.serwylo.emergencies.data.incidents.Incident;

import java.util.HashMap;
import java.util.Map;

public class IconFactory {

	private static Map<String, Integer> knownIcons = new HashMap<String, Integer>();
	private static Map<String, Integer> knownPinIcons = new HashMap<String, Integer>();
	private static Map<String, Integer> knownSmallPinIcons = new HashMap<String, Integer>();
	private static Map<String, Integer> knownLargePinIcons = new HashMap<String, Integer>();

	private static Map<Integer, Drawable> drawableCache = new HashMap<Integer, Drawable>();

	static {
		knownIcons.put( "warningEvacuation", R.drawable.fire_evacuate );
		knownIcons.put( "warningAdvice", R.drawable.fire_info );
		knownIcons.put( "warningWatchAct", R.drawable.fire_watch_and_act );
		knownIcons.put( "plannedBurn", R.drawable.fire_planned );
		knownIcons.put( "fireActive", R.drawable.fire );
		knownIcons.put( "fire", R.drawable.fire_controlled );

		knownPinIcons.put( "warningEvacuation", R.drawable.pin_fire_evacuate );
		knownPinIcons.put( "warningAdvice", R.drawable.pin_fire_info );
		knownPinIcons.put( "warningWatchAct", R.drawable.pin_fire_watch_and_act );
		knownPinIcons.put( "plannedBurn", R.drawable.pin_fire_planned );
		knownPinIcons.put( "fireActive", R.drawable.pin_fire );
		knownPinIcons.put( "fire", R.drawable.pin_fire_controlled );

		knownSmallPinIcons.put( "warningEvacuation", R.drawable.pin_fire_evacuate_small );
		knownSmallPinIcons.put( "warningAdvice", R.drawable.pin_fire_info_small );
		knownSmallPinIcons.put( "warningWatchAct", R.drawable.pin_fire_watch_and_act_small );
		knownSmallPinIcons.put( "plannedBurn", R.drawable.pin_fire_planned_small );
		knownSmallPinIcons.put( "fireActive", R.drawable.pin_fire_small );
		knownSmallPinIcons.put( "fire", R.drawable.pin_fire_controlled_small );
		
		knownLargePinIcons.put( "warningEvacuation", R.drawable.pin_fire_evacuate_large );
		knownLargePinIcons.put( "warningAdvice", R.drawable.pin_fire_info_large );
		knownLargePinIcons.put( "warningWatchAct", R.drawable.pin_fire_watch_and_act_large );
		knownLargePinIcons.put( "plannedBurn", R.drawable.pin_fire_planned_large );
		knownLargePinIcons.put( "fireActive", R.drawable.pin_fire_large );
		knownLargePinIcons.put( "fire", R.drawable.pin_fire_controlled_large );
	}

	private final Context context;

	private IconFactory( Context context ) {
		this.context = context;
	}

	private Drawable get( int resource ) {
		Drawable drawable;
		if ( !drawableCache.containsKey( resource ) ) {
			drawable = context.getResources().getDrawable( resource );
			drawableCache.put( resource, drawable );
		} else {
			drawable = drawableCache.get( resource );
		}
		return drawable;
	}

	public Drawable getIcon( Incident incident ) {
		return get( getDrawableResource( knownIcons, incident, R.drawable.fire ) );
	}

	public Drawable getSmallMapMarker( Incident incident ) {
		return get( getDrawableResource( knownSmallPinIcons, incident, R.drawable.pin_fire_small ) );
	}

	public Drawable getMediumMapMarker( Incident incident ) {
		return get( getDrawableResource( knownPinIcons, incident, R.drawable.pin_fire ) );
	}

	public Drawable getLargeMapMarker( Incident incident ) {
		return get( getDrawableResource( knownLargePinIcons, incident, R.drawable.pin_fire_large ) );
	}

	private static int getDrawableResource( Map<String, Integer> knownResources, Incident incident, int defaultResource ) {
		int drawable;
		if ( knownResources.containsKey( incident.getIcon() ) ) {
			drawable = knownResources.get( incident.getIcon() );
		} else {
			drawable = defaultResource;
		}
		return drawable;
	}

	private static IconFactory singleton = null;

	public static IconFactory instance( Context context ) {
		if ( singleton == null ) {
			singleton = new IconFactory( context );
		}
		return singleton;
	}
}
