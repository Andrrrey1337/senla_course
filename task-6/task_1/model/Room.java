package task_1.model;


import task_1.IdGenerator.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final long id;
    private int number;
    private int capacity;
    private int stars;
    private double price;
    private Guest guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private RoomStatus status;
    public final List<Residence> residenceHistory = new ArrayList<>();

    public Room(int number, double price, int capacity, int stars) {
        this.id = IdGenerator.next();
        this.number = number;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.stars = stars;
    }

    public Room(long id, int number, double price, int capacity, int stars, RoomStatus status) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.status = status;
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

    public void addResidence(Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        residenceHistory.add(new Residence(id, guest,checkInDate,checkOutDate));
        if (residenceHistory.size() > 3) {
            residenceHistory.removeFirst();
        }
    }

    public long getId() {
        return id;
    }

    public List<Residence> getResidence() {
        return new ArrayList<>(residenceHistory);
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
