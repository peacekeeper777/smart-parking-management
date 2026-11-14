package parking.model;
public class Bike extends Vehicle {
    public Bike(String number, String owner) { super(number, owner); }
    @Override public String getType() { return "Bike"; }
    @Override public double getHourlyRate() { return 20.0; }
}
