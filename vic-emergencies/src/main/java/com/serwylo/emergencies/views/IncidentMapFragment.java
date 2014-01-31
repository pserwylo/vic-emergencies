package com.serwylo.emergencies.views;

import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.serwylo.emergencies.*;
import com.serwylo.emergencies.data.*;
import org.osmdroid.*;
import org.osmdroid.events.*;
import org.osmdroid.tileprovider.tilesource.*;
import org.osmdroid.util.*;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.mylocation.*;

import java.util.*;

public class IncidentMapFragment extends Fragment implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem>, IMyLocationConsumer, MapListener {

	private MapView map;
	private ItemizedIconOverlay<OverlayItem> overlay;
	private List<Incident> incidents;
	private IconSize currentIconSize;

    public void setIncidentList( List<Incident> incidents ) {
        this.incidents = incidents;
        refreshIncidentOverlay();
    }

	private enum IconSize {
		SMALL,
		MEDIUM,
		LARGE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setHasOptionsMenu( true );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

		View view = inflater.inflate( R.layout.incident_map, null );
		map = (MapView)view.findViewById( R.id.map );
		map.setTileSource( TileSourceFactory.MAPQUESTOSM );
		map.getController().setCenter( new GeoPoint( -37.813611, 144.963056 ) );
		map.getController().setZoom( 8 );
		map.setMultiTouchControls( true );
		map.setScrollableAreaLimit( new BoundingBoxE6( -28.108326, 153.826447, -39.402244, 128.968506 ) );
		map.setBuiltInZoomControls( true );
		map.setMapListener( this );

		currentIconSize = getPreferredIconSize();
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
		overlay = new ItemizedIconOverlay<OverlayItem>( new ArrayList<OverlayItem>(), this, resourceProxy );
		map.getOverlayManager().add( overlay );

		// In case somebody passed us some incidents before we were created...
		refreshIncidentOverlay();

		return view;

	}

	private IconSize getPreferredIconSize() {
		if ( map.getZoomLevel() < 10 ) {
			return IconSize.SMALL;
		} else if ( map.getZoomLevel() < 13 ) {
			return IconSize.MEDIUM;
		} else {
			return IconSize.LARGE;
		}
	}

	private void refreshIncidentOverlay() {
        // If we haven't navigated to this tab yet, then we don't have a reference to the overlay object.
        if ( overlay != null ) {
            overlay.removeAllItems();
            overlay.addItems(createItemOverlays());
        }
	}

	private List<OverlayItem> createItemOverlays() {

		IconFactory factory = IconFactory.instance( getActivity() );

		List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		if ( incidents != null ) {
			for ( Incident incident : incidents ) {
				for ( Location location : incident.getLocations() ) {
					OverlayItem item = new OverlayItem( Long.toString( incident.getId() ), incident.getTitle(), incident.getStatus(), new GeoPoint( location.getLatitude(), location.getLongitude() ) );
					Drawable marker;
					if ( currentIconSize == IconSize.SMALL ) {
						marker = factory.getSmallMapMarker( incident );
					} else if ( currentIconSize == IconSize.MEDIUM ) {
						marker = factory.getMediumMapMarker( incident );
					} else {
						marker = factory.getLargeMapMarker( incident );
					}
					item.setMarker( marker );
					item.setMarkerHotspot( OverlayItem.HotspotPlace.BOTTOM_CENTER );
					overlayItems.add( item );
				}
			}
		}

		return overlayItems;
	}

	@Override
	public boolean onItemSingleTapUp( int index, OverlayItem item ) {
		Long id = Long.parseLong( item.getUid() );
		for ( Incident incident : incidents ) {
			if ( incident.getId() == id ) {

				AdviceDialog.showAdviceIfAvailable( getActivity(), incident );

				String title = incident.getName() + " - " + incident.getLocationsString();
				Toast.makeText( getActivity(), title, Toast.LENGTH_LONG ).show();

				return true;

			}
		}
		return false;
	}

	@Override
	public boolean onItemLongPress( int index, OverlayItem item ) {
		return false;
	}

	@Override
	public void onLocationChanged( android.location.Location location, IMyLocationProvider source ) {

	}

	public void centerMapOn( Incident incident ) {
		Location location = incident.getLocations().size() > 0 ? incident.getLocations().get( 0 ) : null;
		if ( location != null ) {
			map.getController().setZoom( 10 );
			map.getController().animateTo( new GeoPoint( location.getLatitude(), location.getLongitude() ) );
		}
	}

	@Override
	public boolean onScroll( ScrollEvent event ) {
		return false;
	}

	@Override
	public boolean onZoom( ZoomEvent event ) {
		IconSize preferredIconSize = getPreferredIconSize();
		if ( preferredIconSize != currentIconSize ) {
			currentIconSize = preferredIconSize;
			refreshIncidentOverlay();
		}
		return false;
	}
}
