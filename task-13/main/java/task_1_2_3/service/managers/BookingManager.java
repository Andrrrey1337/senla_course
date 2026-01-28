package task_1_2_3.service.managers;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Inject;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.db.ConnectionManager;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.exceptions.HotelException;
import task_1_2_3.model.Guest;
import task_1_2_3.model.Room;
import task_1_2_3.model.RoomStatus;
import task_1_2_3.util.constants.BusinessMessages;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Singleton
public class BookingManager {
    private static final Logger logger = LoggerFactory.getLogger(BookingManager.class);

    @Inject
    private RoomManager roomManager;

    @Inject
    private GuestManager guestManager;

    @Inject
    private ResidenceManager residenceManager;

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
            logger.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }
}
