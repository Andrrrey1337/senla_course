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

@Component
@Singleton
public class BookingManager {

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
        } catch (DaoException e) {
            try { ConnectionManager.getInstance().rollbackTransaction(); } catch (Exception ignored) {}
            throw new HotelException(e.getMessage(), e);
        } catch (HotelException e) {
            try { ConnectionManager.getInstance().rollbackTransaction(); } catch (Exception ignored) {}
            throw e;
        } catch (RuntimeException e) {
            try { ConnectionManager.getInstance().rollbackTransaction(); } catch (Exception ignored) {}
            throw e;
        }
    }

    public void checkOut(int number) throws HotelException {
        Room room = roomManager.getRoomDetails(number);

        if (RoomStatus.OCCUPIED != room.getStatus()) {
            throw new HotelException(BusinessMessages.ROOM_NOT_OCCUPIED_PREFIX + number + BusinessMessages.ROOM_NOT_OCCUPIED_SUFFIX);
        }

        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckInDate(null);
        room.setCheckOutDate(null);

        roomManager.persist(room);
    }
}
