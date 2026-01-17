package task_2_3_4.model;

import java.time.LocalDate;

public class Residence {
    private final long id;
    private final long guestId;
    private final long roomId;
    public LocalDate checkInDate;
    public LocalDate checkOutDate;

    public Residence(long id, long guestId, long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public long getId() {
        return id;
    }

    public long getGuestId() {
        return guestId;
    }

    public long getRoomId() {
        return roomId;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }
}
