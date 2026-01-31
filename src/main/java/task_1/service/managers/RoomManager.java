package task_1.service.managers;

import task_1.annotations.*;
import task_1.dao.GuestDao;
import task_1.dao.RoomDao;
import task_1.exceptions.DaoException;
import task_1.exceptions.HotelException;
import task_1.model.Guest;
import task_1.model.Room;
import task_1.model.RoomStatus;
import task_1.util.IdGenerator;
import task_1.util.constants.BusinessMessages;
import task_1.util.constants.CommonConstants;
import task_1.util.constants.ConfigConstants;
import task_1.db.ConnectionManager;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@Singleton
public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);

    @ConfigProperty(propertyName = ConfigConstants.ROOM_STATUS_CHANGE_ENABLED, type = ConfigType.BOOLEAN)
    private boolean isAllowChangeStatus;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private RoomDao roomDao;

    @Inject
    private GuestDao guestDao;

    public void addRoom(int number, double price, int capacity, int stars) throws HotelException {
        if (price < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();
            if (roomDao.findByNumber(number).isPresent()) {
                throw new HotelException(BusinessMessages.ROOM_ALREADY_EXISTS_PREFIX + number + BusinessMessages.ROOM_ALREADY_EXISTS_SUFFIX);
            }
            Room room = new Room(idGenerator.next(), number, price, capacity, stars);
            roomDao.create(room);
            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public void updatePriceRoom(int number, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            Room room = getRoomDetails(number);
            room.setPrice(newPrice);
            roomDao.update(room);

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }

    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        if (!isAllowChangeStatus) {
            throw new HotelException(BusinessMessages.ROOM_STATUS_CHANGE_DISABLED);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            Room room = getRoomDetails(number);
            room.setStatus(status);
            roomDao.update(room);

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public double getPaymentForRoom(int number) {
        try {
            return roomDao.findByNumber(number)
                    .map(Room::calculatePayment)
                    .orElse(0.0);
        } catch (DaoException e) {
            logger.error("Ошибка расчета оплаты для номера {}: {}", number, e.getMessage(), e);
            return 0.0;
        }
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return getAllRooms().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
    }

    public Room getRoomDetails(int number) throws HotelException {
        try {
            return roomDao.findByNumber(number)
                    .orElseThrow(() -> new HotelException(BusinessMessages.ROOM_NOT_FOUND_PREFIX + number + BusinessMessages.ROOM_NOT_FOUND_SUFFIX));
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<Room> getAllRooms() {
        try {
            return roomDao.findAll();
        } catch (DaoException e) {
            logger.error("Ошибка при получении списка всех комнат: {}", e.getMessage(), e);
            return List.of();
        }
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
        try {
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
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    void persist(Room room) throws HotelException {
        try {
            roomDao.update(room);
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            System.err.println("Не удалось выполнить rollback: " + e.getMessage());
        }
    }
}
