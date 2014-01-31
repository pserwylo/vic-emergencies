package com.serwylo.emergencies.views;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.serwylo.emergencies.R;
import com.serwylo.emergencies.data.Incident;
import com.serwylo.emergencies.data.Location;
import com.serwylo.emergencies.data.SeverityComparator;
import com.serwylo.emergencies.views.adapters.IncidentAdapter;
import com.serwylo.emergencies.views.utils.IncidentLoader;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IncidentMapFragment extends Fragment implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem>, IMyLocationConsumer, MapListener {

	private MapView map;
	private ItemizedIconOverlay<OverlayItem> overlay;
	private List<Incident> incidents;
	private IconSize currentIconSize;

    public void setIncidentList( List<Incident> incidents ) {
        this.incidents = incidents;
        if ( this.incidents != null ) {
            Collections.sort( this.incidents, Collections.reverseOrder( new SeverityComparator() ) );
        }
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

		incidents = null;
		new IncidentLoader( getActivity() ) {
			@Override
			public void onPostExecute( List<Incident> result ) {
                setIncidentList( result );
			}
		}.execute();

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
