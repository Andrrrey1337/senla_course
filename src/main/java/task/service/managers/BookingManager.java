package task.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import task.db.ConnectionManager;
import task.exceptions.DaoException;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.util.constants.BusinessMessages;

import java.time.LocalDate;

@Service
public class BookingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingManager.class);

    private final RoomManager roomManager;
    private final GuestManager guestManager;
    private final ResidenceManager residenceManager;

    public BookingManager(RoomManager roomManager, GuestManager guestManager, ResidenceManager residenceManager) {
        this.roomManager = roomManager;
        this.guestManager = guestManager;
        this.residenceManager = residenceManager;
    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        try {
            ConnectionManager cm = ConnectionManager.getInstance();
            cm.beginTransaction();

            Room room = roomManager.getRoomDetails(number);

            if (RoomStatus.AVAILABLE != room.getStatus()) {
                throw new HotelException(BusinessMessages.ROOM_NOT_AVAILABLE_PREFIX + number + BusinessMessages.ROOM_NOT_AVAILABLE_SUFFIX);
            }

            if (checkInDate == null || checkOutDate == null || checkOutDate.isBefore(checkInDate)) {
                throw new HotelException(BusinessMessages.CHECKIN_AFTER_CHECKOUT);
            }

            Guest guest = guestManager.createOrFindGuest(guestName);

            room.setGuest(guest);
            room.setStatus(RoomStatus.OCCUPIED);
            room.setCheckInDate(checkInDate);
            room.setCheckOutDate(checkOutDate);

            roomManager.persist(room);

            residenceManager.createResidence(guest.getId(), room.getId(), checkInDate, checkOutDate);

            cm.commitTransaction();
        } catch (DaoException | HotelException | RuntimeException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public void checkOut(int number) throws HotelException {
        try {
            ConnectionManager.getInstance().beginTransaction();

            Room room = roomManager.getRoomDetails(number);

            if (RoomStatus.OCCUPIED != room.getStatus()) {
                throw new HotelException(BusinessMessages.ROOM_NOT_OCCUPIED_PREFIX + number + BusinessMessages.ROOM_NOT_OCCUPIED_SUFFIX);
            }

            room.setGuest(null);
            room.setStatus(RoomStatus.AVAILABLE);
            room.setCheckInDate(null);
            room.setCheckOutDate(null);

            roomManager.persist(room);

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException | RuntimeException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            LOGGER.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }
}
