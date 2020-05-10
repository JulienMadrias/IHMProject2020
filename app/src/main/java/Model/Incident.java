package Model;

public class Incident extends Alert {
    public Incident(double longitude, double latitude, String title, String description){
        super(longitude, latitude, title, description, 1);
    }
}
