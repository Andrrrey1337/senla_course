package task_1.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "number", unique = true, nullable = false)
    private int number;

    @Column(name = "price")
    private double price;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "stars")
    private int stars;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    public Room() {}

    public Room(long id, int number, double price, int capacity, int stars) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.stars = stars;
    }

    public Room(long id, int number, double price, int capacity, int stars, RoomStatus status, LocalDate checkInDate,
                LocalDate checkOutDate) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.status = status;
        this.capacity = capacity;
        this.stars = stars;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public double calculatePayment() {
        if (checkInDate != null && checkOutDate != null) {
            return price * java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0.0;

    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStars() {
        return stars;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }


    @Override
    public String toString() {
        String guestName = (guest != null) ? guest.getName() : "None";
        return """
                Номер: %s
                Кол-во звезд: %d
                Вместительность: %d
                Стоимость: %f
                Статус номера: %s
                Имя гостя: %s
                """.formatted(number, stars, capacity, price, status, guestName);
    }
}
