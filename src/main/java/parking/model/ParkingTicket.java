package parking.model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ParkingTicket {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Vehicle vehicle;
    private final int slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    public ParkingTicket(Vehicle vehicle, int slotNumber) { this.vehicle = vehicle; this.slotNumber = slotNumber; this.entryTime = LocalDateTime.now(); }
    public static ParkingTicket restore(Vehicle vehicle, int slotNumber, LocalDateTime entry, LocalDateTime exit, double fee) {
        ParkingTicket ticket = new ParkingTicket(vehicle, slotNumber);
        ticket.entryTime = entry;
        ticket.exitTime = exit;
        ticket.fee = fee;
        return ticket;
    }
    public Vehicle getVehicle() { return vehicle; }
    public int getSlotNumber() { return slotNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getFee() { return fee; }
    public boolean isActive() { return exitTime == null; }
    public double close(LocalDateTime when) {
        if (!isActive()) throw new IllegalStateException("Ticket is already closed.");
        exitTime = when;
        long minutes = Math.max(1, Duration.between(entryTime, exitTime).toMinutes());
        long hours = Math.max(1, (minutes + 59) / 60);
        fee = hours * vehicle.getHourlyRate();
        return fee;
    }
    public String toCsv() {
        return csv(vehicle.getRegistrationNumber()) + "," + csv(vehicle.getOwnerName()) + "," + vehicle.getType() + "," + slotNumber + "," + entryTime.format(FORMAT) + "," + (exitTime == null ? "" : exitTime.format(FORMAT)) + "," + String.format(java.util.Locale.ROOT, "%.2f", fee);
    }
    private static String csv(String s) { return "\"" + s.replace("\"", "\"\"") + "\""; }
}
