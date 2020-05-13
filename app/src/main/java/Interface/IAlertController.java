package Interface;

import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;


public interface IAlertController<Type> {
    Type getByOverlayItem(OverlayItem overlayItem);
    ArrayList<Type> get();
    void post();
}
