package task_1;

import java.time.LocalDate;

public class Residence {
    public Guest guest;
    public LocalDate checkInDate;
    public LocalDate checkOutDate;

    public Residence(Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
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
