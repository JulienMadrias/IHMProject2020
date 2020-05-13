package Model;

public class Accident extends Alert{
    private int casualtyNumber;
    public Accident(double longitude, double latitude, String title, String description, int casualtyNumber){
        super(longitude, latitude, title, description, Alert.ACCIDENT);
        this.casualtyNumber = casualtyNumber;
    }
    public int getCasualtyNumber() {
        return casualtyNumber;
    }
}
