package com.example.ihmproject.alertfactory;

public class Incident extends Alert {
    public Incident(int longitude, int latitude, String title, String description){
        super(longitude, latitude, title, description, 1);
    }
}
