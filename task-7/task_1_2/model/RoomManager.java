package model;

import config.ConfigManager;
import exceptions.HotelException;
import util.IdGenerator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class RoomManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final IdGenerator idGeneratorState;

    public RoomManager(IdGenerator idGenerator) {
        this.idGeneratorState = idGenerator;
    }

    public void addRoom(int number, double price, int capacity, int stars) throws HotelException {
        if (price < 0 || capacity <= 0 || stars <= 0) {
            throw new HotelException("Цена, вместимость и количество звезд должны быть положительными");
        }
        if (rooms.containsKey(number)) {
            throw new HotelException("Комната с номером " + number + " уже существует");
        }
        rooms.put(number, new Room(idGeneratorState.next(), number, price, capacity, stars));
    }

    public void updatePriceRoom(int number, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        room.setPrice(newPrice);
    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        try {
            if (!ConfigManager.getInstance().isRoomStatusChangeEnabled()) {
                throw new HotelException("Возможность изменять статус номера отключена в конфигурации.");
            }
        } catch (IOException e) {
            throw new HotelException("Ошибка при загрузке конфигурации: " + e.getMessage(), e);
        }
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        room.setStatus(status);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> getSortedRooms(Comparator<Room> comparator) {
        return rooms.values().stream()
                .sorted(comparator)
                .toList();
    }

    public List<Room> getAvailableRooms(Comparator<Room> comparator) {
        return rooms.values().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public List<Room> getRoomAvailableByDate(LocalDate date) {
        return rooms.values().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus() ||
                        (room.getCheckOutDate() != null && date.isAfter(room.getCheckOutDate())))
                .toList();
    }

    public List<Room> getGuests(Comparator<Room> comparator) {
        return rooms.values().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public long getCountAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus())
                .count();
    }

    public long getCountGuests() {
        return rooms.values().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
                .count();
    }

    public double getPaymentForRoom(int number) {
        Room room = rooms.get(number);
        return room.calculatePayment();
    }

    public List<Residence> getThreeLastGuests(int number) {
        Room room = rooms.get(number);
        return room.getResidence();
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
    }

    public Room getRoomDetails(int number) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        return room;
    }

    // методы для DataManager
    public void updateOrCreateRoom(long id, int number, double price, int capacity, int stars,
                                   RoomStatus status, long guestId, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        Room foundById = rooms.values().stream()
                .filter(room -> room.getId() == id)
                .findFirst()
                .orElse(null);

        if (foundById != null) {
            updateExistingRoomById(foundById, id, number, price, capacity, stars, status,
                    guestId, checkInDate, checkOutDate);
        } else {
            updateOrCreateRoomByNumber(id, number, price, capacity, stars, status, guestId,
                    checkInDate, checkOutDate);
        }
    }

    private void updateExistingRoomById(Room foundById, long id, int number, double price, int capacity, int stars,
                                        RoomStatus status, long guestId, LocalDate checkInDate, LocalDate checkOutDate) {
        int oldNumber = foundById.getNumber();
        foundById.setNumber(number);
        foundById.setPrice(price);
        foundById.setCapacity(capacity);
        foundById.setStars(stars);
        foundById.setStatus(status);
        foundById.setCheckInDate(checkInDate);
        foundById.setCheckOutDate(checkOutDate);
        if (oldNumber != number) {
            rooms.put(number, foundById);
            rooms.remove(oldNumber);
        }
    }

    private void updateOrCreateRoomByNumber(long id, int number, double price, int capacity, int stars,
                                            RoomStatus status, long guestId, LocalDate checkInDate, LocalDate checkOutDate)
                                            throws HotelException
    {
        if (rooms.containsKey(number)) {
            Room rExist = rooms.get(number);
            rExist.setPrice(price);
            rExist.setCapacity(capacity);
            rExist.setStars(stars);
            rExist.setStatus(status);
            rExist.setCheckInDate(checkInDate);
            rExist.setCheckOutDate(checkOutDate);
        } else {
            Room room = new Room(id, number, price, capacity, stars, status, checkInDate, checkOutDate);
            if (GuestManager.guestExists(guestId)) {
                room.setGuest(GuestManager.getGuestById(guestId));
            }
            rooms.put(number, room);
        }
    }
}