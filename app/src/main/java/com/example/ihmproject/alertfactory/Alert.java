package com.example.ihmproject.alertfactory;

public class Alert {
    private int longitude;
    private int latitude;
    private int type; //1==incident, 2==accident
    private String title;
    private String description;

    public Alert(int longitude, int latitude, String title, String description, int type){
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.title = title;
        this.type = type;
    }

    public int getLongitude(){return longitude;}

    public int getLatitude(){return latitude;}

    public int getType(){return type;}

    public String getTitle(){return title;}

    public String getDescription(){return description;}
}
