package Model;

public class Accident extends Alert{
    public Accident(double longitude, double latitude, String title, String description){
        super(longitude, latitude, title, description, 2);
    }
}
