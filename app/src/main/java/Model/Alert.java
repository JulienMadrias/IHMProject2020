package Model;

public class Alert {
    private double longitude;
    private double latitude;
    private int type; //1==incident, 2==accident
    private String title;
    private String description;

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
}
