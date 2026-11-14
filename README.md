# Parkwise — Smart Parking Management System

A beginner-friendly Core Java and Swing project. It uses only the JDK: no frameworks, database, or downloaded libraries.

## Project structure

```text
smart-parking-management/
├── src/main/java/parking/
│   ├── core/       ParkingManager, ParkingException, BookingTask
│   ├── model/      Vehicle, Car, Bike, Truck, ParkingSlot, ParkingTicket
│   ├── ui/         ParkingApplication (Swing desktop UI)
│   └── util/       RecordStore (CSV file storage)
├── docs/           Report, tests, presentation and viva material
├── data/           Created at runtime; parking-records.csv
└── run.sh
```

## Requirements and run

Install JDK 8 or newer. From the project root:

```sh
./run.sh
```

On Windows, compile with `javac -d out src/main/java/parking/model/*.java src/main/java/parking/core/*.java src/main/java/parking/util/*.java src/main/java/parking/ui/*.java` and launch with `java -cp out parking.ui.ParkingApplication`.

Records are written to `data/parking-records.csv`. The UI starts with 12 slots and reloads records on the next launch. Keep the CSV with the application to retain history.

## Architecture and Java concepts

- `Vehicle` is an abstract class; `Car`, `Bike`, and `Truck` inherit it and override type and hourly rate. This demonstrates abstraction, inheritance, and runtime polymorphism.
- Fields are private, constructor inputs are validated, and getters expose state: encapsulation.
- `ParkingManager` owns slot and ticket `ArrayList`s. Its synchronized `park` operation checks duplicates and reserves the first free slot as one atomic operation. `exit` similarly releases the slot.
- `BookingTask implements Runnable` is the optional concurrency demonstration class. It can be used by a Java caller to launch separate `Thread`s that make real calls to the shared manager; synchronized allocation prevents double booking. The everyday dashboard focuses on actual vehicle operations and does not create demo bookings.
- `ParkingException` represents expected booking and exit errors. Swing catches them and shows readable messages.
- `RecordStore` reads and writes CSV through JDK file APIs. It handles quoted commas and quotes in owner and registration fields.
- `ParkingTicket` records entry and exit times. Billing rounds up to a whole hour, with a one-hour minimum: bike ₹20/hr, car ₹40/hr, truck ₹60/hr.

## Demo checklist

1. Register and park a car, bike, and truck; observe slot and dashboard updates.
2. Try the same registration twice; duplicate booking is rejected.
3. Search the active vehicle, then record its exit; history and revenue update.
4. Use **Add slot** and **Remove slot** to adjust capacity. Capacity cannot fall below occupied slots.
5. Close and relaunch; active stays and completed history reload from CSV.

## Limitations / extension ideas

This is a single-machine teaching demo. It stores data in a CSV file and is designed to be used by one application process. A multi-process production system would need transactional shared storage. The slot capacity is set in the manager constructor; modify `12` in `ParkingApplication` to change it.
