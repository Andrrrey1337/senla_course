package task.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.dao.GuestDao;
import task.dao.RoomDao;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;
import task.util.constants.CommonConstants;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class RoomManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomManager.class);

    private final boolean isAllowChangeStatus;
    private final IdGenerator idGenerator;
    private final RoomDao roomDao;
    private final GuestDao guestDao;

    public RoomManager(@Value("${room.status.change.enabled}") boolean isAllowChangeStatus,
                       IdGenerator idGenerator, RoomDao roomDao, GuestDao guestDao) {
        this.isAllowChangeStatus = isAllowChangeStatus;
        this.idGenerator = idGenerator;
        this.roomDao = roomDao;
        this.guestDao = guestDao;
    }

    public void addRoom(int number, double price, int capacity, int stars) throws HotelException {
        if (price < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        if (roomDao.findByNumber(number).isPresent()) {
            throw new HotelException(BusinessMessages.ROOM_ALREADY_EXISTS_PREFIX + number + BusinessMessages.ROOM_ALREADY_EXISTS_SUFFIX);
        }
        Room room = new Room(idGenerator.next(), number, price, capacity, stars);
        roomDao.create(room);
        LOGGER.info("Добавлен новый номер: {}, вместимость: {}, цена: {}", number, capacity, price);
    }

    public void updatePriceRoom(int number, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        Room room = getRoomDetails(number);
        room.setPrice(newPrice);
        roomDao.update(room);
        LOGGER.info("Стоимость номера {} изменена на: {}", number, newPrice);

    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        if (!isAllowChangeStatus) {
            throw new HotelException(BusinessMessages.ROOM_STATUS_CHANGE_DISABLED);
        }
        Room room = getRoomDetails(number);
        room.setStatus(status);
        roomDao.update(room);
        LOGGER.info("Статус номера {} изменен на: {}", number, status);
    }

    public double getPaymentForRoom(int number) {
        return roomDao.findByNumber(number)
                .map(Room::calculatePayment)
                .orElse(0.0);
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
    }

    public Room getRoomDetails(int number) throws HotelException {
        return roomDao.findByNumber(number)
                .orElseThrow(() -> new HotelException(BusinessMessages.ROOM_NOT_FOUND_PREFIX + number + BusinessMessages.ROOM_NOT_FOUND_SUFFIX));
    }

    public List<Room> getAllRooms() {
        return roomDao.findAll();
    }

    public List<Room> getSortedRooms(Comparator<Room> comparator) {
        return getAllRooms().stream()
                .sorted(comparator)
                .toList();
    }

    public List<Room> getAvailableRooms(Comparator<Room> comparator) {
        return getAllRooms().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public List<Room> getRoomAvailableByDate(LocalDate date) {
        return getAllRooms().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus() ||
                        (room.getCheckOutDate() != null && date.isAfter(room.getCheckOutDate())))
                .toList();
    }

    public List<Room> getGuests(Comparator<Room> comparator) {
        return getAllRooms().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public long getCountAvailableRooms() {
        return getAllRooms().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus())
                .count();
    }

    public long getCountGuests() {
        return getAllRooms().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
                .count();
    }

    // методы для DataManager
    public void updateOrCreateRoom(long id, int number, double price, int capacity, int stars,
                                   RoomStatus status, long guestId,
                                   LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        if (price < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        Optional<Room> byId = roomDao.findById(id);

        Room room = new Room(id, number, price, capacity, stars, status, checkInDate, checkOutDate);

        if (guestId > 0) {
            Guest guest = guestDao.findById(guestId).orElse(new Guest(guestId, CommonConstants.EMPTY_STRING));
            room.setGuest(guest);
        } else {
            room.setGuest(null);
        }

        if (byId.isPresent()) {
            roomDao.update(room);
        } else {
            roomDao.create(room);
        }
    }

    void persist(Room room) throws HotelException {
        roomDao.update(room);
    }
}
