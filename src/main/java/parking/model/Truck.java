package parking.model;
public class Truck extends Vehicle {
    public Truck(String number, String owner) { super(number, owner); }
    @Override public String getType() { return "Truck"; }
    @Override public double getHourlyRate() { return 60.0; }
}
