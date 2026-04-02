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
import task.dto.GuestDto;
import task.model.Guest;
import task.service.managers.GuestManager;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GuestControllerTest {
    @Mock
    private GuestManager guestManager;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private GuestController guestController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void getGuest_Success() throws Exception {
        when(guestManager.getAllGuests()).thenReturn(List.of(
                new Guest(1L, "Иван"),
                new Guest(2L, "Петр")
        ));

        mockMvc.perform(get("/api/guests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].name").value("Петр"));

        verify(guestManager, times(1)).getAllGuests();
    }

    @Test
    public void getGuestById_Success() throws Exception {
        long guestId = 1L;
        Guest guest = new Guest(guestId, "Иван");

        when(guestManager.getGuestById(guestId)).thenReturn(guest);

        mockMvc.perform(get("/api/guests/{id}", guestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guest.getId()))
                .andExpect(jsonPath("$.name").value(guest.getName()));

        verify(guestManager, times(1)).getGuestById(guestId);
    }

    @Test
    public void getGuestByName_Success() throws Exception {
        String guestName = "Иван";
        Guest guest = new Guest(1L, guestName);

        when(guestManager.getGuestByName(guestName)).thenReturn(guest);

        mockMvc.perform(get("/api/guests/search")
                        .param("name", guestName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(guestName));

        verify(guestManager, times(1)).getGuestByName(guestName);
    }

    @Test
    public void createGuest_Success() throws Exception {
        String guestName = "Иван";
        Guest guest = new Guest(1L, guestName);

        GuestDto guestDto = new GuestDto();
        guestDto.setName(guestName);

        when(guestManager.createOrFindGuest(guestName)).thenReturn(guest);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(guestName));

        verify(guestManager, times(1)).createOrFindGuest(guestName);
    }

    @Test
    public void createGuest_InvalidName() throws Exception {
        String invalidName = "";
        GuestDto requestDto = new GuestDto();
        requestDto.setName(invalidName);

        String errorMessage = "Имя гостя не может быть пустым";

        when(guestManager.createOrFindGuest(invalidName)).thenThrow(new task.exceptions.HotelException(errorMessage));

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(guestManager, times(1)).createOrFindGuest(invalidName);
    }

    @Test
    public void updateGuest_Success() throws Exception {
        long guestId = 1L;
        String guestName = "Иван";

        GuestDto guestDto = new GuestDto();
        guestDto.setName(guestName);

        mockMvc.perform(put("/api/guests/{id}", guestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isOk());

        verify(guestManager, times(1)).updateOrCreateGuest(guestId, guestName);
    }

    @Test
    public void updateGuest_GuestNotFound() throws Exception {
        long invalidGuestId = 999L;
        String newName = "Новое Имя";

        GuestDto requestDto = new GuestDto();
        requestDto.setName(newName);

        String errorMessage = "Гость с ID " + invalidGuestId + " не найден";

        // для void методов используем другую конструкцию
        doThrow(new task.exceptions.HotelException(errorMessage))
                .when(guestManager).updateOrCreateGuest(invalidGuestId, newName);

        mockMvc.perform(put("/api/guests/{id}", invalidGuestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(guestManager, times(1)).updateOrCreateGuest(invalidGuestId, newName);
    }
}
