package Interface;

import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;

import Model.Incident;

public interface IIAccidentModelView {
    Incident getIncidentToPublish();
    void addMaker(GeoPoint startPoint, String title, Drawable icon, boolean userPosition);
}
