package task_1.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Residence implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    public Guest guest;
    public LocalDate checkInDate;
    public LocalDate checkOutDate;

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
