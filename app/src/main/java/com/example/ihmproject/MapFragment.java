package com.example.ihmproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapFragment extends Fragment implements View.OnClickListener {
    private FloatingActionButton eventAdder,incidentButton,accidentButton;
    private TextView incidentButtonText, accidentButtonText;

    private Animation fabOpenAnim, fabCloseAnim, floatButtonOpen, floatButtonClose;

    private boolean isAddEventsOpen;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        IMapController mapController;
        ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
        eventAdder = (FloatingActionButton) view.findViewById(R.id.addAnEvent);
        incidentButton = (FloatingActionButton) view.findViewById(R.id.incidentButton);
        accidentButton = (FloatingActionButton) view.findViewById(R.id.accidentButton);
        incidentButtonText = (TextView) view.findViewById(R.id.incidentTextView);
        accidentButtonText = (TextView) view.findViewById(R.id.accidentTextView);

        eventAdder.setOnClickListener(this);
        incidentButton.setOnClickListener(this);
        accidentButton.setOnClickListener(this);

        isAddEventsOpen = false;

        Context context;
        fabOpenAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);
        floatButtonOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_open);
        floatButtonClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_close);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addAnEvent:
                if(isAddEventsOpen){
                    eventAdder.setAnimation(floatButtonClose);
                    incidentButtonText.setVisibility(View.INVISIBLE);
                    accidentButtonText.setVisibility(View.INVISIBLE);
                    incidentButton.setAnimation(fabCloseAnim);
                    accidentButton.setAnimation(fabCloseAnim);
                    isAddEventsOpen = false;
                }else {
                    eventAdder.setAnimation(floatButtonOpen);
                    incidentButtonText.setVisibility(View.VISIBLE);
                    accidentButtonText.setVisibility(View.VISIBLE);
                    incidentButton.setAnimation(fabOpenAnim);
                    accidentButton.setAnimation(fabOpenAnim);
                    isAddEventsOpen = true;
                }
                break;
                case R.id.incidentButton:
                Snackbar.make(v, "Button d'incident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
                case R.id.accidentButton:
                Snackbar.make(v, "Button d'accident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }
}
