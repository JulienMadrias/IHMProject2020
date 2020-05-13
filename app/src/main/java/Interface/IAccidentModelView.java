package Interface;

import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;

import Model.Accident;
import Model.Incident;

public interface IAccidentModelView {
    void addMaker(GeoPoint startPoint, String title, Drawable icon, boolean userPosition);
    Accident getAccidentToPublish();
}
