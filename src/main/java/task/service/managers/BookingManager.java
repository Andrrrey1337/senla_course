package task.service.managers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.util.constants.BusinessMessages;

import java.time.LocalDate;

@Service
@Transactional
public class BookingManager {
    private final RoomManager roomManager;
    private final GuestManager guestManager;
    private final ResidenceManager residenceManager;

    public BookingManager(RoomManager roomManager, GuestManager guestManager, ResidenceManager residenceManager) {
        this.roomManager = roomManager;
        this.guestManager = guestManager;
        this.residenceManager = residenceManager;
    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
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
