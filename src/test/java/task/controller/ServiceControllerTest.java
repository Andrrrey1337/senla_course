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
import task.dto.ServiceDto;
import task.exceptions.HotelException;
import task.model.Service;
import task.service.managers.ServiceManager;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ServiceControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ServiceManager serviceManager;

    @InjectMocks
    private ServiceController serviceController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(serviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void getServices_DefaultSortByName_Success() throws Exception {
        Service service = new Service(1L, "Завтрак", 500.0);

        when(serviceManager.getAllServices()).thenReturn(List.of(service));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Завтрак"))
                .andExpect(jsonPath("$[0].price").value(500.0));

        verify(serviceManager, times(1)).getAllServices();
        verify(serviceManager, never()).getAllServicesSortedByPrice();
    }

    @Test
    public void getServices_SortByPrice_Success() throws Exception {
        Service service = new Service(2L, "Уборка", 300.0);

        when(serviceManager.getAllServicesSortedByPrice()).thenReturn(List.of(service));

        mockMvc.perform(get("/api/services")
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Уборка"));

        verify(serviceManager, times(1)).getAllServicesSortedByPrice();
        verify(serviceManager, never()).getAllServices();
    }

    @Test
    public void getServices_ManagerThrowsException_ReturnsBadRequest() throws Exception {
        String errorMessage = "Внутренняя ошибка базы данных: список услуг временно недоступен";

        when(serviceManager.getAllServices())
                .thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(serviceManager, times(1)).getAllServices();
        verify(serviceManager, never()).getAllServicesSortedByPrice();
    }


    @Test
    public void createService_Success() throws Exception {
        String serviceName = "Прачечная";
        double price = 1000.0;

        ServiceDto requestDto = new ServiceDto();
        requestDto.setName(serviceName);
        requestDto.setPrice(price);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(serviceManager, times(1)).addService(serviceName, price);
    }

    @Test
    public void createService_DuplicateName() throws Exception {
        String serviceName = "Завтрак";
        double price = 500.0;

        ServiceDto requestDto = new ServiceDto();
        requestDto.setName(serviceName);
        requestDto.setPrice(price);

        String errorMessage = "Услуга с таким названием уже существует";

        doThrow(new HotelException(errorMessage))
                .when(serviceManager).addService(serviceName, price);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(serviceManager, times(1)).addService(serviceName, price);
    }


    @Test
    public void updateServicePrice_Success() throws Exception {
        String serviceName = "Бассейн";
        double newPrice = 1500.0;

        mockMvc.perform(put("/api/services/{name}/price", serviceName)
                        .param("price", String.valueOf(newPrice)))
                .andExpect(status().isOk());

        verify(serviceManager, times(1)).updatePriceService(serviceName, newPrice);
    }

    @Test
    public void updateServicePrice_ServiceNotFound() throws Exception {
        String invalidServiceName = "Неизвестная услуга";
        double newPrice = 100.0;
        String errorMessage = "Услуга " + invalidServiceName + " не найдена";

        doThrow(new HotelException(errorMessage))
                .when(serviceManager).updatePriceService(invalidServiceName, newPrice);

        mockMvc.perform(put("/api/services/{name}/price", invalidServiceName)
                        .param("price", String.valueOf(newPrice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(serviceManager, times(1)).updatePriceService(invalidServiceName, newPrice);
    }
}