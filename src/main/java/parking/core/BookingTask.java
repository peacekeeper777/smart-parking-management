package parking.core;
import parking.model.ParkingTicket;
import parking.model.Vehicle;
import java.util.function.Consumer;

/** Runnable request used both by the GUI simulation and ordinary Java threads. */
public class BookingTask implements Runnable {
    private final ParkingManager manager;
    private final Vehicle vehicle;
    private final Consumer<String> result;
    public BookingTask(ParkingManager manager, Vehicle vehicle, Consumer<String> result) { this.manager=manager; this.vehicle=vehicle; this.result=result; }
    @Override public void run() {
        String message;
        try { ParkingTicket t=manager.park(vehicle); message=Thread.currentThread().getName()+" — "+vehicle.getRegistrationNumber()+" booked slot "+t.getSlotNumber(); }
        catch(ParkingException e) { message=Thread.currentThread().getName()+" — "+vehicle.getRegistrationNumber()+": "+e.getMessage(); }
        if(result!=null) result.accept(message);
    }
}
