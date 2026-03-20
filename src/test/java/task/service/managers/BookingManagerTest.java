package task.service.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingManagerTest {
    @Mock
    private RoomManager roomManager;
    @Mock
    private GuestManager guestManager;
    @Mock
    private ResidenceManager residenceManager;

    @InjectMocks
    private BookingManager bookingManager;


    //checkIn
    @Test
    public void checkIn_Success() {
        int roomNumber = 101;
        String guestName = "Иван Иванов";
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(3);
        Room room = new Room(1L, roomNumber, 2000.0, 2, 4);
        room.setStatus(RoomStatus.AVAILABLE);
        Guest guest = new Guest(1L, guestName);

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);
        when(guestManager.createOrFindGuest(guestName)).thenReturn(guest);

        bookingManager.checkIn(roomNumber, guestName, checkInDate, checkOutDate);

        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(guest, room.getGuest());
        assertEquals(checkInDate, room.getCheckInDate());
        assertEquals(checkOutDate, room.getCheckOutDate());

        verify(roomManager, times(1)).persist(room);
        verify(residenceManager, times(1)).createResidence(guest.getId(), room.getId(), checkInDate, checkOutDate);
    }

    @Test
    public void checkIn_RoomStatusNotAvailable() {
        int roomNumber = 101;
        String guestName = "Иван Иванов";
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(3);
        Room room = new Room(1L, roomNumber, 2000.0, 2, 4);
        room.setStatus(RoomStatus.OCCUPIED);

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);

        assertThrows(HotelException.class, () -> bookingManager.checkIn(roomNumber, guestName, checkInDate, checkOutDate));

        verify(guestManager, never()).createOrFindGuest(anyString());
        verify(roomManager, never()).persist(room);
    }

    @Test
    public void checkIn_CheckOutIsBeforeCheckIn() {
        int roomNumber = 101;
        String guestName = "Иван Иванов";
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().minusDays(3);
        Room room = new Room(1L, roomNumber, 2000.0, 2, 4);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);

        assertThrows(HotelException.class, () -> bookingManager.checkIn(roomNumber, guestName, checkInDate, checkOutDate));

        verify(roomManager, never()).persist(room);
    }

    // checkOut
    @Test
    public void checkOut_Success() {
        int roomNumber = 101;
        Room room = new Room(1L, roomNumber, 2000.0, 2, 4);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setGuest(new Guest(1L, "Гость"));
        room.setCheckInDate(LocalDate.now().minusDays(2));
        room.setCheckOutDate(LocalDate.now());

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);

        bookingManager.checkOut(roomNumber);

        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertNull(room.getGuest());
        assertNull(room.getCheckInDate());
        assertNull(room.getCheckOutDate());

        verify(roomManager, times(1)).persist(room);
    }

    @Test
    public void checkOut_RoomStatusNotOccupied() {
        int roomNumber = 101;
        Room room = new Room(1L, roomNumber, 2000.0, 2, 4);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setGuest(new Guest(1L, "Гость"));
        room.setCheckInDate(LocalDate.now().minusDays(2));
        room.setCheckOutDate(LocalDate.now());

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);

        assertThrows(HotelException.class, () -> bookingManager.checkOut(roomNumber));

        verify(roomManager, never()).persist(room);
    }
}
