package task.service.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dao.ResidenceDao;
import task.exceptions.HotelException;
import task.model.Residence;
import task.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResidenceManagerTest {

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private ResidenceDao residenceDao;

    private ResidenceManager residenceManager;
    private final int maxHistorySize = 5;

    @BeforeEach
    public void setUp() {
        residenceManager = new ResidenceManager(maxHistorySize, idGenerator, residenceDao);
    }

    @Test
    public void createResidence_Success() {
        long guestId = 1L;
        long roomId = 101L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        Residence expectedResidence = new Residence(1L, guestId, roomId, checkIn, checkOut);

        when(idGenerator.next()).thenReturn(1L);
        when(residenceDao.create(any(Residence.class))).thenReturn(expectedResidence);

        Residence result = residenceManager.createResidence(guestId, roomId, checkIn, checkOut);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(guestId, result.getGuestId());
        assertEquals(roomId, result.getRoomId());

        verify(residenceDao, times(1)).create(any(Residence.class));
    }

    @Test
    public void createResidence_CheckInAfterCheckOut() {
        long guestId = 1L;
        long roomId = 101L;
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now();

        assertThrows(HotelException.class, () -> residenceManager.createResidence(guestId, roomId, checkIn, checkOut));

        verify(residenceDao, never()).create(any());
    }

    @Test
    public void getLastByRoom_Success() {
        long roomId = 101L;

        when(residenceDao.findLastByRoom(roomId, maxHistorySize))
                .thenReturn(List.of(new Residence(), new Residence()));

        List<Residence> result = residenceManager.getLastByRoom(roomId);

        assertEquals(2, result.size());
        verify(residenceDao, times(1)).findLastByRoom(roomId, maxHistorySize);
    }

    @Test
    public void getLastByRoom_NegativeMaxHistorySize() {
        ResidenceManager negativeLimitManager = new ResidenceManager(-5, idGenerator, residenceDao);
        long roomId = 101L;

        when(residenceDao.findLastByRoom(roomId, 0)).thenReturn(List.of());

        List<Residence> result = negativeLimitManager.getLastByRoom(roomId);

        assertTrue(result.isEmpty());

        verify(residenceDao, times(1)).findLastByRoom(roomId, 0);
    }
}