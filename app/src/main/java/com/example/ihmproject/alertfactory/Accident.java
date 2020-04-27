package com.example.ihmproject.alertfactory;

public class Accident extends Alert{
    public Accident(int longitude, int latitude, String title, String description){
        super(longitude, latitude, title, description, 1);
    }
}
