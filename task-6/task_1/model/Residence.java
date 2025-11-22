package task_1.model;

import task_1.IdGenerator.IdGenerator;

import java.time.LocalDate;

public class Residence {
    private final long id;
    public Guest guest;
    public LocalDate checkInDate;
    public LocalDate checkOutDate;

    public Residence(Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = IdGenerator.next();
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Residence(long id, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public long getId() {
        return id;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }


}
