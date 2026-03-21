package task.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.service.DataManager;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DataManager dataManager;

    @InjectMocks
    private DataController dataController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dataController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void exportGuests_Success() throws Exception {
        mockMvc.perform(post("/api/data/export/guests"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).exportGuests();
    }

    @Test
    public void exportRooms_Success() throws Exception {
        mockMvc.perform(post("/api/data/export/rooms"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).exportRooms();
    }

    @Test
    public void exportServices_Success() throws Exception {
        mockMvc.perform(post("/api/data/export/services"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).exportServices();
    }

    @Test
    public void exportServiceRecords_Success() throws Exception {
        mockMvc.perform(post("/api/data/export/service-records"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).exportServiceRecords();
    }

    @Test
    public void importGuests_Success() throws Exception {
        mockMvc.perform(post("/api/data/import/guests"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).importGuests();
    }

    @Test
    public void importRooms_Success() throws Exception {
        mockMvc.perform(post("/api/data/import/rooms"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).importRooms();
    }

    @Test
    public void importServices_Success() throws Exception {
        mockMvc.perform(post("/api/data/import/services"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).importServices();
    }

    @Test
    public void importServiceRecords_Success() throws Exception {
        mockMvc.perform(post("/api/data/import/service-records"))
                .andExpect(status().isOk());

        verify(dataManager, times(1)).importServiceRecords();
    }
}