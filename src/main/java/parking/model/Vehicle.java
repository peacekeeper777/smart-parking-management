package parking.model;

/** Abstract base class: common vehicle data is kept private and validated. */
public abstract class Vehicle {
    private final String registrationNumber;
    private final String ownerName;

    protected Vehicle(String registrationNumber, String ownerName) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) throw new IllegalArgumentException("Registration number is required.");
        if (ownerName == null || ownerName.trim().isEmpty()) throw new IllegalArgumentException("Owner name is required.");
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.ownerName = ownerName.trim();
    }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getOwnerName() { return ownerName; }
    public abstract String getType();
    public abstract double getHourlyRate();
    @Override public String toString() { return getType() + " " + registrationNumber + " (" + ownerName + ")"; }
}
