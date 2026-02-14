package task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "residence")
public class Residence {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "check_in_date")
    public LocalDate checkInDate;

    @Column(name = "check_out_date")
    public LocalDate checkOutDate;

    public Residence() { }

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
