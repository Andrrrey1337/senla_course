package task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.dto.OrderServiceDto;
import task.exceptions.HotelException;
import task.model.ServiceRecord;
import task.service.managers.ServiceRecordManager;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ServiceRecordsControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private ServiceRecordManager serviceRecordManager;

    @InjectMocks
    private ServiceRecordsController serviceRecordsController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(serviceRecordsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void getAllRecords_Success() throws Exception {
        LocalDate date = LocalDate.now();
        ServiceRecord record = new ServiceRecord(1L, 10L, 20L, date);

        when(serviceRecordManager.getAllRecords()).thenReturn(List.of(record));

        mockMvc.perform(get("/api/service-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].guestId").value(10L))
                .andExpect(jsonPath("$[0].serviceId").value(20L))
                .andExpect(jsonPath("$[0].date").value(date.toString()));

        verify(serviceRecordManager, times(1)).getAllRecords();
    }

    @Test
    public void getRecordsByGuestName_DefaultSortByDate_Success() throws Exception {
        String guestName = "Иван";
        LocalDate date = LocalDate.now();
        ServiceRecord record = new ServiceRecord(1L, 10L, 20L, date);

        when(serviceRecordManager.getGuestServicesSortedByDate(guestName)).thenReturn(List.of(record));

        mockMvc.perform(get("/api/service-records/{guestName}", guestName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(serviceRecordManager, times(1)).getGuestServicesSortedByDate(guestName);
        verify(serviceRecordManager, never()).getGuestServicesSortedByPrice(anyString());
    }

    @Test
    public void getRecordsByGuestName_SortByPrice_Success() throws Exception {
        String guestName = "Иван";
        LocalDate date = LocalDate.now();
        ServiceRecord record = new ServiceRecord(2L, 10L, 20L, date);

        when(serviceRecordManager.getGuestServicesSortedByPrice(guestName)).thenReturn(List.of(record));

        mockMvc.perform(get("/api/service-records/{guestName}", guestName)
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(2L));

        verify(serviceRecordManager, times(1)).getGuestServicesSortedByPrice(guestName);
        verify(serviceRecordManager, never()).getGuestServicesSortedByDate(anyString());
    }

    @Test
    public void getRecordsByGuestName_GuestNotFound() throws Exception {
        String invalidGuestName = "Неизвестный Гость";
        String errorMessage = "Гость с именем " + invalidGuestName + " не найден";

        when(serviceRecordManager.getGuestServicesSortedByDate(invalidGuestName))
                .thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/service-records/{guestName}", invalidGuestName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(serviceRecordManager, times(1)).getGuestServicesSortedByDate(invalidGuestName);
        verify(serviceRecordManager, never()).getGuestServicesSortedByPrice(anyString());
    }

    @Test
    public void orderService_Success() throws Exception {
        String guestName = "Иван";
        String serviceName = "Завтрак";
        LocalDate date = LocalDate.now();

        OrderServiceDto requestDto = new OrderServiceDto();
        requestDto.setGuestName(guestName);
        requestDto.setServiceName(serviceName);
        requestDto.setDate(date);

        mockMvc.perform(post("/api/service-records/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(serviceRecordManager, times(1)).orderService(guestName, serviceName, date);
    }

    @Test
    public void orderService_ServiceNotFound() throws Exception {
        String guestName = "Иван";
        String invalidServiceName = "Неизвестная услуга";
        LocalDate date = LocalDate.now();

        OrderServiceDto requestDto = new OrderServiceDto();
        requestDto.setGuestName(guestName);
        requestDto.setServiceName(invalidServiceName);
        requestDto.setDate(date);

        String errorMessage = "Услуга " + invalidServiceName + " не найдена";

        doThrow(new HotelException(errorMessage))
                .when(serviceRecordManager).orderService(guestName, invalidServiceName, date);

        mockMvc.perform(post("/api/service-records/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(serviceRecordManager, times(1)).orderService(guestName, invalidServiceName, date);
    }
}