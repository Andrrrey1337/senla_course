package service.managers;

import exceptions.HotelException;
import model.Guest;
import model.Room;
import model.RoomStatus;

import java.io.Serializable;
import java.time.LocalDate;

public class BookingManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RoomManager roomManager;
    private final GuestManager guestManager;

    public BookingManager(RoomManager roomManager, GuestManager guestManager) {
        this.roomManager = roomManager;
        this.guestManager = guestManager;
    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        Room room = roomManager.getRoomDetails(number);
        if (RoomStatus.AVAILABLE != room.getStatus()) {
            throw new HotelException("Комната с номером " + number + " недоступна для заселения (статус: " + room.getStatus() + ")");
        }
        if (checkInDate.isAfter(checkOutDate)) {
            throw new HotelException("Дата заселения не может быть позже даты выселения");
        }
        Guest guest = guestManager.createOrFindGuest(guestName);
        room.setGuest(guest);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setCheckInDate(checkInDate);
        room.setCheckOutDate(checkOutDate);
        room.addResidence(guest, checkInDate, checkOutDate);
    }

    public void checkOut(int number) throws HotelException {
        Room room = roomManager.getRoomDetails(number);
        if (RoomStatus.OCCUPIED != room.getStatus()) {
            throw new HotelException("Комната с номером " + number + " не занята, невозможно выполнить выселение");
        }
        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckOutDate(null);
    }
}