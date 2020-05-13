package Model;

import java.io.FileWriter;
import java.util.ArrayList;

public class Incident extends Alert {
    private ArrayList<String> listOfPostImages = new ArrayList<>();
    public Incident(double longitude, double latitude, String title, String description){
        this(longitude, latitude, title, description, new ArrayList<String>());
    }
    public Incident(double longitude, double latitude, String title, String description, ArrayList<String> listOfPostImages){
        super(longitude, latitude, title, description, Alert.INCIDENT);
        this.listOfPostImages = new ArrayList<>(listOfPostImages);
    }

    public ArrayList<String> getListOfPostImages() {
        return listOfPostImages;
    }
}
