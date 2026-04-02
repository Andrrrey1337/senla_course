package task.service.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dao.GuestDao;
import task.dao.RoomDao;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.util.IdGenerator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomManagerTest {

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private RoomDao roomDao;
    @Mock
    private GuestDao guestDao;

    private RoomManager roomManager;

    @BeforeEach
    public void setUp() {
        roomManager = new RoomManager(true, idGenerator, roomDao, guestDao);
    }

    @Test
    public void addRoom_Success() {
        int roomNumber = 101;
        double price = 1000.0;
        int capacity = 2;
        int stars = 3;

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.empty());
        when(idGenerator.next()).thenReturn(1L);

        roomManager.addRoom(roomNumber, price, capacity, stars);

        verify(roomDao, times(1)).create(any(Room.class));
    }

    @Test
    public void addRoom_PriceNegative() {
        assertThrows(HotelException.class, () -> roomManager.addRoom(101, -100.0, 2, 3));
        verify(roomDao, never()).create(any());
    }

    @Test
    public void addRoom_RoomAlreadyExists() {
        int roomNumber = 101;

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.of(new Room()));

        assertThrows(HotelException.class, () -> roomManager.addRoom(roomNumber, 1000.0, 2, 3));
        verify(roomDao, never()).create(any());
    }

    @Test
    public void updatePriceRoom_Success() {
        int roomNumber = 101;
        double newPrice = 1500.0;
        Room room = new Room(1L, roomNumber, 1000.0, 2, 3);

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.of(room));

        roomManager.updatePriceRoom(roomNumber, newPrice);

        assertEquals(newPrice, room.getPrice());
        verify(roomDao, times(1)).update(room);
    }

    @Test
    public void updatePriceRoom_PriceNegative() {
        assertThrows(HotelException.class, () -> roomManager.updatePriceRoom(101, -500.0));
        verify(roomDao, never()).update(any());
    }

    @Test
    public void setRoomStatus_Success() {
        int roomNumber = 101;
        Room room = new Room(1L, roomNumber, 1000.0, 2, 3);

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.of(room));

        roomManager.setRoomStatus(roomNumber, RoomStatus.REPAIR);

        assertEquals(RoomStatus.REPAIR, room.getStatus());
        verify(roomDao, times(1)).update(room);
    }

    @Test
    public void setRoomStatus_ChangeDisabled() {
        RoomManager disabledRoomManager = new RoomManager(false, idGenerator, roomDao, guestDao);

        assertThrows(HotelException.class, () -> disabledRoomManager.setRoomStatus(101, RoomStatus.REPAIR));
    }

    @Test
    public void getPaymentForRoom_Success() {
        int roomNumber = 101;
        Room room = new Room(1L, roomNumber, 1000.0, 2, 3);
        room.setCheckInDate(LocalDate.now().minusDays(3));
        room.setCheckOutDate(LocalDate.now());

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.of(room));

        double payment = roomManager.getPaymentForRoom(roomNumber);

        assertEquals(3000.0, payment);
    }

    @Test
    public void getAllRoomsSortedByPrice_Success() {
        Room room1 = new Room(1L, 101, 3000.0, 2, 3);
        Room room2 = new Room(2L, 102, 1000.0, 2, 3);
        Room room3 = new Room(3L, 103, 2000.0, 2, 3);

        when(roomDao.findAll()).thenReturn(List.of(room1, room2, room3));

        List<Room> result = roomManager.getAllRoomsSortedByPrice();

        assertEquals(3, result.size());
        assertEquals(102, result.get(0).getNumber());
        assertEquals(103, result.get(1).getNumber());
        assertEquals(101, result.get(2).getNumber());
    }

    @Test
    public void getRoomDetails_Success() {
        int roomNumber = 101;
        Room room = new Room(1L, roomNumber, 1000.0, 2, 3);

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.of(room));

        Room result = roomManager.getRoomDetails(roomNumber);

        assertEquals(room, result);
    }

    @Test
    public void getRoomDetails_RoomNotFound() {
        int roomNumber = 101;

        when(roomDao.findByNumber(roomNumber)).thenReturn(Optional.empty());

        assertThrows(HotelException.class, () -> roomManager.getRoomDetails(roomNumber));
    }

    @Test
    public void getAllRooms_Success() {
        when(roomDao.findAll()).thenReturn(List.of(new Room(), new Room()));

        List<Room> rooms = roomManager.getAllRooms();

        assertEquals(2, rooms.size());
    }

    @Test
    public void getSortedRooms_Success() {
        Room room1 = new Room(1L, 101, 1000.0, 2, 3);
        Room room2 = new Room(2L, 102, 1000.0, 4, 3);

        when(roomDao.findAll()).thenReturn(List.of(room2, room1));

        List<Room> result = roomManager.getSortedRooms(Comparator.comparing(Room::getNumber));

        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getNumber());
        assertEquals(102, result.get(1).getNumber());
    }

    @Test
    public void getAvailableRooms_Success() {
        Room room1 = new Room(1L, 101, 1000.0, 2, 3);
        Room room2 = new Room(2L, 102, 2000.0, 2, 3);
        room2.setStatus(RoomStatus.OCCUPIED);

        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        List<Room> result = roomManager.getAvailableRooms(Comparator.comparing(Room::getNumber));

        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getNumber());
    }

    @Test
    public void getRoomAvailableByDate_Success() {
        LocalDate date = LocalDate.now().plusDays(5);

        Room availableRoom = new Room(1L, 101, 1000.0, 2, 3);
        availableRoom.setStatus(RoomStatus.AVAILABLE);

        Room occupiedWillBeFree = new Room(2L, 102, 2000.0, 2, 3);
        occupiedWillBeFree.setStatus(RoomStatus.OCCUPIED);
        occupiedWillBeFree.setCheckOutDate(LocalDate.now().plusDays(2));

        Room occupiedWontBeFree = new Room(3L, 103, 3000.0, 2, 3);
        occupiedWontBeFree.setStatus(RoomStatus.OCCUPIED);
        occupiedWontBeFree.setCheckOutDate(LocalDate.now().plusDays(10));

        when(roomDao.findAll()).thenReturn(List.of(availableRoom, occupiedWillBeFree, occupiedWontBeFree));

        List<Room> result = roomManager.getRoomAvailableByDate(date);

        assertEquals(2, result.size());
        assertTrue(result.contains(availableRoom));
        assertTrue(result.contains(occupiedWillBeFree));
    }

    @Test
    public void getGuests_Success() {
        Room room1 = new Room(1L, 101, 1000.0, 2, 3);
        room1.setStatus(RoomStatus.OCCUPIED);

        Room room2 = new Room(2L, 102, 2000.0, 2, 3);
        room2.setStatus(RoomStatus.AVAILABLE);

        Room room3 = new Room(3L, 103, 500.0, 2, 3);
        room3.setStatus(RoomStatus.OCCUPIED);

        when(roomDao.findAll()).thenReturn(List.of(room1, room2, room3));

        List<Room> result = roomManager.getGuests(Comparator.comparing(Room::getPrice));

        assertEquals(2, result.size());
        assertEquals(103, result.get(0).getNumber());
        assertEquals(101, result.get(1).getNumber());
    }

    @Test
    public void getCountAvailableRooms_Success() {
        Room room1 = new Room(1L, 101, 1000.0, 2, 3);
        room1.setStatus(RoomStatus.AVAILABLE);

        Room room2 = new Room(2L, 102, 1000.0, 2, 3);
        room2.setStatus(RoomStatus.OCCUPIED);

        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        long count = roomManager.getCountAvailableRooms();

        assertEquals(1L, count);
    }

    @Test
    public void getCountGuests_Success() {
        Room room1 = new Room(1L, 101, 1000.0, 2, 3);
        room1.setStatus(RoomStatus.OCCUPIED);

        Room room2 = new Room(2L, 102, 1000.0, 2, 3);
        room2.setStatus(RoomStatus.OCCUPIED);

        Room room3 = new Room(3L, 103, 1000.0, 2, 3);
        room3.setStatus(RoomStatus.AVAILABLE);

        when(roomDao.findAll()).thenReturn(List.of(room1, room2, room3));

        long count = roomManager.getCountGuests();

        assertEquals(2L, count);
    }

    @Test
    public void updateOrCreateRoom_CreateSuccess() {
        long roomId = 1L;
        int roomNumber = 101;
        double price = 1000.0;

        when(roomDao.findById(roomId)).thenReturn(Optional.empty());

        roomManager.updateOrCreateRoom(roomId, roomNumber, price, 2, 3, RoomStatus.AVAILABLE, 0L, null, null);

        verify(roomDao, times(1)).create(any(Room.class));
        verify(roomDao, never()).update(any(Room.class));
    }

    @Test
    public void updateOrCreateRoom_UpdateSuccessWithGuest() {
        long roomId = 1L;
        int roomNumber = 101;
        double price = 1000.0;
        long guestId = 5L;
        Guest guest = new Guest(guestId, "Гость");
        Room existingRoom = new Room(roomId, roomNumber, price, 2, 3);

        when(roomDao.findById(roomId)).thenReturn(Optional.of(existingRoom));
        when(guestDao.findById(guestId)).thenReturn(Optional.of(guest));

        roomManager.updateOrCreateRoom(roomId, roomNumber, price, 2, 3, RoomStatus.OCCUPIED, guestId, LocalDate.now(), LocalDate.now().plusDays(1));

        verify(roomDao, times(1)).update(any(Room.class));
        verify(roomDao, never()).create(any(Room.class));
    }

    @Test
    public void persist_Success() {
        Room room = new Room(1L, 101, 1000.0, 2, 3);

        roomManager.persist(room);

        verify(roomDao, times(1)).update(room);
    }
}