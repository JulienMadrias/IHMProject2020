package com.example.ihmproject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        IMapController mapController;
        ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
        assert container != null;
        MapView map = view.findViewById(R.id.map);
        if(map != null){
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            mapController = map.getController();
            mapController.setZoom(18.0);
            GeoPoint startPoint = new GeoPoint(43.65020, 7.00517);
            mapController.setCenter(startPoint);

            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            OverlayItem home = new OverlayItem("F. Rallo", "nos bureaux", new GeoPoint(43.65020,7.00517));
            Drawable m = home.getMarker(0);

            items.add(home);
            items.add(new OverlayItem("Resto", "chez babar", new GeoPoint(43.64950,7.00517))); // Lat/Lon decimal degrees

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(inflater.getContext(), items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            return true;
                        }
                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    });


            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);
        }

        return view;
    }
}
