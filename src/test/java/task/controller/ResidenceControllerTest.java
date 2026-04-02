package task.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.exceptions.HotelException;
import task.model.Residence;
import task.model.Room;
import task.service.managers.ResidenceManager;
import task.service.managers.RoomManager;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ResidenceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResidenceManager residenceManager;
    @Mock
    private RoomManager roomManager;

    @InjectMocks
    private ResidenceController residenceController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(residenceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void getRoomHistory_Success() throws Exception {
        int roomNumber = 101;
        long roomId = 5L;
        long guestId = 10L;

        Room room = new Room();
        room.setId(roomId);
        room.setNumber(roomNumber);

        LocalDate checkIn = LocalDate.now().minusDays(3);
        LocalDate checkOut = LocalDate.now();
        Residence residence = new Residence(1L, guestId, roomId, checkIn, checkOut);

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);
        when(residenceManager.getLastByRoom(roomId)).thenReturn(List.of(residence));

        mockMvc.perform(get("/api/residences/room/{roomNumber}", roomNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].guestId").value(guestId))
                .andExpect(jsonPath("$[0].roomId").value(roomId))
                .andExpect(jsonPath("$[0].checkInDate").value(checkIn.toString()))
                .andExpect(jsonPath("$[0].checkOutDate").value(checkOut.toString()));

        verify(roomManager, times(1)).getRoomDetails(roomNumber);
        verify(residenceManager, times(1)).getLastByRoom(roomId);
    }

    @Test
    public void getRoomHistory_RoomNotFound() throws Exception {
        int roomNumber = 66;

        when(roomManager.getRoomDetails(roomNumber)).thenThrow(new HotelException("Комната с номером 66 не найдена"));

        mockMvc.perform(get("/api/residences/room/{roomNumber}", roomNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Комната с номером 66 не найдена"));

        verify(roomManager, times(1)).getRoomDetails(roomNumber);
        verify(residenceManager, never()).getLastByRoom(anyLong());
    }
}