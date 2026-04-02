package task.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import task.exceptions.HotelException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // специальный контроллер только для этого теста для генерации ошибок
    @RestController
    private static class DummyController {

        @GetMapping("/test/hotel-error")
        public void throwHotelException() {
            throw new HotelException("Тестовая бизнес-ошибка");
        }

        @GetMapping("/test/general-error")
        public void throwGeneralException() throws Exception {
            throw new Exception("Сбой подключения к БД");
        }
    }

    @BeforeEach
    public void setUp() {
        // mockMvc с нашим новым контроллером и реальным обработчиком
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void handleHotelException_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/test/hotel-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Тестовая бизнес-ошибка"));
    }

    @Test
    public void handleGeneralException_ReturnsInternalServerError() throws Exception {
        mockMvc.perform(get("/test/general-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера: Сбой подключения к БД"));
    }
}