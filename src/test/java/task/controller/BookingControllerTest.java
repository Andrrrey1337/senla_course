package task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.dto.CheckInRequest;
import task.service.managers.BookingManager;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(
            new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()
    );

    @Mock
    private BookingManager bookingManager;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void checkIn_Success() throws Exception {
        int roomNumber = 101;
        String guestName = "Иван Иванов";
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(5);

        CheckInRequest requestBody = new CheckInRequest(roomNumber, guestName, checkInDate,checkOutDate);

        mockMvc.perform(post("/api/bookings/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated());

        verify(bookingManager, times(1)).checkIn(roomNumber, guestName, checkInDate, checkOutDate);
    }

    @Test
    public void checkIn_RoomOccupied_ThrowsException() throws Exception {
        int roomNumber = 101;
        String guestName = "Иван Иванов";
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(5);

        task.dto.CheckInRequest request = new task.dto.CheckInRequest(roomNumber, guestName, checkInDate, checkOutDate);

        String errorMessage = "Комната " + roomNumber + " уже занята на эти даты";

        doThrow(new task.exceptions.HotelException(errorMessage))
                .when(bookingManager).checkIn(roomNumber, guestName, checkInDate, checkOutDate);

        mockMvc.perform(post("/api/bookings/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(bookingManager, times(1)).checkIn(roomNumber, guestName, checkInDate, checkOutDate);
    }

    @Test
    public void checkOut_Success() throws Exception {
        int roomNumber = 101;

        mockMvc.perform(post("/api/bookings/check-out/{roomNumber}", roomNumber))
                .andExpect(status().isOk());

        verify(bookingManager, times(1)).checkOut(roomNumber);
    }

    @Test
    public void checkOut_RoomNotOccupied() throws Exception {
        int roomNumber = 101;
        String errorMessage = "В комнате " + roomNumber + " сейчас никто не проживает";

        doThrow(new task.exceptions.HotelException(errorMessage))
                .when(bookingManager).checkOut(roomNumber);

        mockMvc.perform(post("/api/bookings/check-out/{roomNumber}", roomNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(bookingManager, times(1)).checkOut(roomNumber);
    }
}