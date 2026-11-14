package parking.core;

import parking.model.*;
import parking.util.RecordStore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** All shared state changes are synchronized so a slot cannot be sold twice. */
public class ParkingManager {
    private final ArrayList<ParkingSlot> slots = new ArrayList<ParkingSlot>();
    private final ArrayList<ParkingTicket> history = new ArrayList<ParkingTicket>();
    private final RecordStore store;
    private double revenue;
    public ParkingManager(int slotCount, RecordStore store) {
        if (slotCount < 1) throw new IllegalArgumentException("At least one slot is required.");
        this.store = store;
        for (int i = 1; i <= slotCount; i++) slots.add(new ParkingSlot(i));
        history.addAll(store.load());
        for (ParkingTicket t : history) {
            if (t.isActive()) {
                while (slots.size() < t.getSlotNumber()) slots.add(new ParkingSlot(slots.size() + 1));
                ParkingSlot slot = slots.get(t.getSlotNumber() - 1);
                if (slot.isAvailable()) slot.occupy(t.getVehicle());
                else System.err.println("Ignoring conflicting active record for slot " + t.getSlotNumber());
            } else revenue += t.getFee();
        }
    }
    public synchronized ParkingTicket park(Vehicle vehicle) throws ParkingException {
        for (ParkingSlot slot : slots) if (!slot.isAvailable() && slot.getVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber())) throw new ParkingException("This vehicle is already parked.");
        for (ParkingSlot slot : slots) if (slot.isAvailable()) {
            slot.occupy(vehicle);
            ParkingTicket ticket = new ParkingTicket(vehicle, slot.getNumber());
            history.add(ticket); persist(); return ticket;
        }
        throw new ParkingException("Parking is full.");
    }
    public synchronized ParkingTicket exit(String registration) throws ParkingException {
        String key = registration.trim().toUpperCase();
        for (ParkingTicket t : history) if (t.isActive() && t.getVehicle().getRegistrationNumber().equals(key)) {
            double fee = t.close(LocalDateTime.now()); slots.get(t.getSlotNumber() - 1).release(); revenue += fee; persist(); return t;
        }
        throw new ParkingException("No active parking found for " + key + ".");
    }
    private void persist() { try { store.save(history); } catch (java.io.IOException e) { System.err.println("Could not save records: " + e.getMessage()); } }
    public synchronized int getTotalSlots() { return slots.size(); }
    public synchronized int getAvailableCount() { int n=0; for(ParkingSlot s:slots) if(s.isAvailable()) n++; return n; }
    public synchronized int getOccupiedCount() { return slots.size()-getAvailableCount(); }
    public synchronized int getVehicleCount() { return history.size(); }
    public synchronized double getRevenue() { return revenue; }
    /** Change capacity while preserving current slot assignments. */
    public synchronized void setCapacity(int capacity) throws ParkingException {
        if (capacity < 1) throw new ParkingException("Capacity must be at least one slot.");
        if (capacity < getOccupiedCount()) throw new ParkingException("Cannot reduce capacity below the " + getOccupiedCount() + " currently occupied slots.");
        while (slots.size() < capacity) slots.add(new ParkingSlot(slots.size() + 1));
        while (slots.size() > capacity) slots.remove(slots.size() - 1);
    }
    public synchronized List<ParkingSlot> getSlots() { return Collections.unmodifiableList(new ArrayList<ParkingSlot>(slots)); }
    public synchronized List<ParkingTicket> getHistory() { return Collections.unmodifiableList(new ArrayList<ParkingTicket>(history)); }
    public synchronized ParkingTicket findActive(String registration) {
        String key=registration.trim().toUpperCase();
        for(ParkingTicket t:history) if(t.isActive() && t.getVehicle().getRegistrationNumber().equals(key)) return t;
        return null;
    }
}
