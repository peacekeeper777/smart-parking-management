package parking.model;
public class Car extends Vehicle {
    public Car(String number, String owner) { super(number, owner); }
    @Override public String getType() { return "Car"; }
    @Override public double getHourlyRate() { return 40.0; }
}
