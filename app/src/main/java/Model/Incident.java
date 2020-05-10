package Model;

import java.util.ArrayList;

public class Incident extends Alert {
    ArrayList<PostImage> listOfPostImages= new ArrayList<>();
    public Incident(double longitude, double latitude, String title, String description){
        super(longitude, latitude, title, description, 1);
    }
    public Incident(double longitude, double latitude, String title, String description, ArrayList<PostImage> listOfPostImages){
        super(longitude, latitude, title, description, 1);
        this.listOfPostImages = new ArrayList<>(listOfPostImages);
    }
}
