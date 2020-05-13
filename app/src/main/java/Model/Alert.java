package Model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.ihmproject.R;

public class Alert {
    private double longitude;
    private double latitude;
    private int type; //1==incident, 2==accident
    private String title;
    private String description;

    public static final int ACCIDENT = 1;
    public static final int INCIDENT = 2;


    public static Drawable getIcon(int type, Context context) throws Throwable {
        switch(type) {
            case ACCIDENT: return  context.getResources().getDrawable(R.drawable.ic_accident_svgrepo_com_marker_big);
            case INCIDENT: return  context.getResources().getDrawable(R.drawable.ic_traffic_jam_svgrepo_com_marker_big);
            default: throw new Throwable("not made");
        }
    }

    public Alert(double longitude, double latitude, String title, String description, int type){
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.title = title;
        this.type = type;
    }

    public double getLongitude(){return longitude;}

    public double getLatitude(){return latitude;}

    public int getType(){return type;}

    public String getTitle(){return title;}

    public String getDescription(){return description;}


    @Override
    public String toString() {
        return "Alert{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
