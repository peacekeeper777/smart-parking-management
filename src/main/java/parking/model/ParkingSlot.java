package parking.model;
public class ParkingSlot {
    private final int number;
    private Vehicle vehicle;
    public ParkingSlot(int number) { this.number = number; }
    public int getNumber() { return number; }
    public Vehicle getVehicle() { return vehicle; }
    public boolean isAvailable() { return vehicle == null; }
    public void occupy(Vehicle vehicle) { if (!isAvailable()) throw new IllegalStateException("Slot is occupied."); this.vehicle = vehicle; }
    public void release() { vehicle = null; }
}
