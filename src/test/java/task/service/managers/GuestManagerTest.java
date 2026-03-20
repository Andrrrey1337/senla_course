package task.service.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dao.GuestDao;
import task.exceptions.HotelException;
import task.model.Guest;
import task.util.IdGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestManagerTest {
    @Mock
    private GuestDao guestDao;
    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GuestManager guestManager;

    @Test
    public void createOrFindGuest_GuestAlreadyExists() {
        String guestName = "Иван Иванов";
        Guest guest = new Guest(1L, guestName);

        when(guestDao.findByName(guestName)).thenReturn(Optional.of(guest));

        Guest result = guestManager.createOrFindGuest(guestName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(guestName, result.getName());

        verify(guestDao, never()).create(any(Guest.class));
        verify(idGenerator, never()).next();
    }

    @Test
    public void createOrFindGuest_GuestDoesNotExist() {
        String guestName = "Петр Петров";
        long guestId = 2L;

        when(guestDao.findByName(guestName)).thenReturn(Optional.empty());
        when(idGenerator.next()).thenReturn(guestId);
        when(guestDao.create(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Guest result = guestManager.createOrFindGuest(guestName);

        assertNotNull(result);
        assertEquals(guestId, result.getId());
        assertEquals(guestName, result.getName());

        verify(guestDao, times(1)).create(any(Guest.class));
    }

    @Test
    public void createOrFindGuest_NameIsEmpty() {
        String guestName = "";

        assertThrows(HotelException.class, () -> guestManager.createOrFindGuest(guestName));

        verify(guestDao, never()).findByName(anyString());
        verify(guestDao, never()).create(any());
    }

    @Test
    public void getGuestByName_Success() {
        String guestName = "Анна Смирнова";
        Guest guest = new Guest(3L, guestName);

        when(guestDao.findByName(guestName)).thenReturn(Optional.of(guest));

        Guest result = guestManager.getGuestByName(guestName);

        assertEquals(guest, result);
        verify(guestDao, times(1)).findByName(guestName);
    }

    @Test
    public void getGuestByName_NameIsEmpty() {
        String guestName = "";

        assertThrows(HotelException.class, () -> guestManager.getGuestByName(guestName));

        verify(guestDao, never()).findByName(anyString());
    }

    @Test
    public void getGuestByName_GuestNotFound() {
        String guestName = "Неизвестный Гость";

        when(guestDao.findByName(guestName)).thenReturn(Optional.empty());

        assertThrows(HotelException.class, () -> guestManager.getGuestByName(guestName));
    }

    @Test
    public void getGuestById_Success() {
        long guestId = 1L;
        Guest guest = new Guest(guestId, "Алексей");

        when(guestDao.findById(guestId)).thenReturn(Optional.of(guest));

        Guest result = guestManager.getGuestById(guestId);

        assertNotNull(result);
        assertEquals(guest.getName(), result.getName());
        verify(guestDao, times(1)).findById(guestId);
    }

    @Test
    public void getGuestById_GuestNotFound() {
        long guestId = 99L;

        when(guestDao.findById(guestId)).thenReturn(Optional.empty());

        assertThrows(HotelException.class, () -> guestManager.getGuestById(guestId));
    }

    @Test
    public void getAllGuests_Success() {
        when(guestDao.findAll()).thenReturn(List.of(
                new Guest(1L, "Гость 1"),
                new Guest(2L, "Гость 2")
        ));

        List<Guest> guests = guestManager.getAllGuests();

        assertEquals(2, guests.size());
        verify(guestDao, times(1)).findAll();
    }

    @Test
    public void updateOrCreateGuest_GuestExists() {
        long guestId = 1L;
        String guestName = "Обновленное Имя";
        Guest guest = new Guest(guestId, "Старое Имя");

        when(guestDao.findById(guestId)).thenReturn(Optional.of(guest));

        guestManager.updateOrCreateGuest(guestId, guestName);

        assertEquals(guestName, guest.getName());
        verify(guestDao, times(1)).update(guest);
        verify(guestDao, never()).create(any());
    }

    @Test
    public void updateOrCreateGuest_GuestDoesNotExist() {
        long guestId = 2L;
        String guestName = "Новый Гость";

        when(guestDao.findById(guestId)).thenReturn(Optional.empty());

        guestManager.updateOrCreateGuest(guestId, guestName);

        verify(guestDao, times(1)).create(any(Guest.class));
        verify(guestDao, never()).update(any());
    }

    @Test
    public void guestExists_Found() {
        long guestId = 1L;

        when(guestDao.findById(guestId)).thenReturn(Optional.of(new Guest(guestId, "Test")));

        assertTrue(guestManager.guestExists(guestId));
    }

    @Test
    public void guestExists_NotFound() {
        long guestId = 99L;

        when(guestDao.findById(guestId)).thenReturn(Optional.empty());

        assertFalse(guestManager.guestExists(guestId));
    }
}