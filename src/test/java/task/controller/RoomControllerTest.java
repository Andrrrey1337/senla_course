package task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.exceptions.HotelException;
import task.model.Room;
import task.model.RoomStatus;
import task.service.managers.RoomManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {
    @Mock
    private RoomManager roomManager;

    @InjectMocks
    private RoomController roomController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(
            new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()
    );

    @Captor
    private ArgumentCaptor<Comparator<Room>> comparatorCaptor;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Room createTestRoom(Long id, int number, double price) {
        Room room = new Room();
        room.setId(id);
        room.setNumber(number);
        room.setPrice(price);
        room.setCapacity(2);
        room.setStars(4);
        room.setStatus(RoomStatus.values()[0]);
        return room;
    }

    private Room createTestRoom(Long id, int number, double price, int capacity, int stars) {
        Room room = new Room();
        room.setId(id);
        room.setNumber(number);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setStars(stars);
        room.setStatus(RoomStatus.values()[0]);
        return room;
    }

    @Test
    public void getRoomByNumber_Success() throws Exception {
        int roomNumber = 101;
        Room room = createTestRoom(1L, roomNumber, 1500.0);

        when(roomManager.getRoomDetails(roomNumber)).thenReturn(room);

        mockMvc.perform(get("/api/rooms/{number}", roomNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(roomNumber))
                .andExpect(jsonPath("$.price").value(1500.0));

        verify(roomManager, times(1)).getRoomDetails(roomNumber);
    }

    @Test
    public void getRoomByNumber_RoomNotFound() throws Exception {
        int invalidRoomNumber = 999;
        String errorMessage = "Комната с номером " + invalidRoomNumber + " не найдена";

        when(roomManager.getRoomDetails(invalidRoomNumber))
                .thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/{number}", invalidRoomNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).getRoomDetails(invalidRoomNumber);
    }

    @Test
    public void getAllRooms_DefaultSort_Success() throws Exception {
        Room roomCheapButHighNumber = createTestRoom(1L, 505, 1000.0);
        Room roomExpensiveButLowNumber = createTestRoom(2L, 101, 5000.0);

        when(roomManager.getSortedRooms(any())).thenReturn(List.of(roomExpensiveButLowNumber, roomCheapButHighNumber));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        // ловим компаратор, который контроллер передал в менеджер
        verify(roomManager, times(1)).getSortedRooms(comparatorCaptor.capture());
        Comparator<Room> capturedComparator = comparatorCaptor.getValue();


        // комната 101 должна быть раньше чем комната 505
        int result = capturedComparator.compare(roomExpensiveButLowNumber, roomCheapButHighNumber);
        assertTrue(result < 0, "Компаратор по умолчанию должен сортировать по номеру комнаты");
    }

    @Test
    public void getAllRooms_SortByPrice_Success() throws Exception {
        Room roomCheapButHighNumber = createTestRoom(1L, 505, 1000.0);
        Room roomExpensiveButLowNumber = createTestRoom(2L, 101, 5000.0);

        when(roomManager.getSortedRooms(any())).thenReturn(List.of(roomCheapButHighNumber, roomExpensiveButLowNumber));

        // по стоимости
        mockMvc.perform(get("/api/rooms").param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(roomManager, times(1)).getSortedRooms(comparatorCaptor.capture());
        Comparator<Room> capturedComparator = comparatorCaptor.getValue();


        // комната с ценой 1000 должна быть меньше, чем комната с ценой 5000
        int result = capturedComparator.compare(roomCheapButHighNumber, roomExpensiveButLowNumber);
        assertTrue(result < 0, "При sortBy=price компаратор должен сортировать по цене");
    }

    @Test
    public void getAllRooms_EmptyList_Success() throws Exception {
        when(roomManager.getSortedRooms(any(Comparator.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(roomManager, times(1)).getSortedRooms(any(Comparator.class));
    }


    @Test
    public void getAvailableRooms_DefaultSort_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 4, 5);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 3);

        when(roomManager.getAvailableRooms(any())).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(roomManager).getAvailableRooms(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room1, room2) < 0); // 101 < 505
    }

    @Test
    public void getAvailableRooms_SortByPrice_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 4, 5);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 3);

        when(roomManager.getAvailableRooms(any())).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/available").param("sortBy", "price"))
                .andExpect(status().isOk());

        // Проверяем сортировку по цене
        verify(roomManager).getAvailableRooms(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room2, room1) < 0); // 1000 < 5000
    }

    @Test
    public void getAvailableRooms_SortByCapacity_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 4, 5);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 3);

        when(roomManager.getAvailableRooms(any())).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/available").param("sortBy", "capacity"))
                .andExpect(status().isOk());

        // по вместимости
        verify(roomManager).getAvailableRooms(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room2, room1) < 0); // 2 < 4
    }

    @Test
    public void getAvailableRooms_SortByStars_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 4, 5);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 3);

        when(roomManager.getAvailableRooms(any())).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/available").param("sortBy", "stars"))
                .andExpect(status().isOk());

        // по звездам
        verify(roomManager).getAvailableRooms(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room2, room1) < 0); // 3 < 5
    }

    @Test
    public void getAvailableRooms_ReturnsBadRequest() throws Exception {
        String errorMessage = "Ошибка при получении свободных комнат";
        when(roomManager.getAvailableRooms(any())).thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void getCountAvailableRooms_Success() throws Exception {
        long expectedCount = 15L;
        when(roomManager.getCountAvailableRooms()).thenReturn(expectedCount);

        mockMvc.perform(get("/api/rooms/available/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(expectedCount));

        verify(roomManager, times(1)).getCountAvailableRooms();
    }

    @Test
    public void getCountAvailableRooms_ReturnsBadRequest() throws Exception {
        String errorMessage = "Не удалось подсчитать комнаты";
        when(roomManager.getCountAvailableRooms()).thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/available/count"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void getRoomAvailableByDate_Success() throws Exception {
        LocalDate searchDate = LocalDate.now().plusDays(2);
        Room room = createTestRoom(1L, 101, 2000.0, 2, 4);

        when(roomManager.getRoomAvailableByDate(searchDate)).thenReturn(List.of(room));

        //  дату в формате YYYY-MM-DD
        mockMvc.perform(get("/api/rooms/available/date")
                        .param("date", searchDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].number").value(101));

        verify(roomManager, times(1)).getRoomAvailableByDate(searchDate);
    }

    @Test
    public void getRoomAvailableByDate_ReturnsBadRequest() throws Exception {
        LocalDate searchDate = LocalDate.now();
        String errorMessage = "Неверный формат даты или ошибка поиска";

        when(roomManager.getRoomAvailableByDate(searchDate))
                .thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/available/date")
                        .param("date", searchDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).getRoomAvailableByDate(searchDate);
    }

    @Test
    public void getOccupiedRooms_DefaultSortByNumber_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 2, 4);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 4);

        when(roomManager.getGuests(any(Comparator.class))).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/occupied"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(roomManager).getGuests(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room1, room2) < 0); // 101 < 505
    }

    @Test
    public void getOccupiedRooms_SortByPrice_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 2, 4);
        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 4);

        when(roomManager.getGuests(any(Comparator.class))).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/occupied")
                        .param("sortBy", "price"))
                .andExpect(status().isOk());

        verify(roomManager).getGuests(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room2, room1) < 0); // 1000 < 5000
    }

    @Test
    public void getOccupiedRooms_SortByDate_Success() throws Exception {
        Room room1 = createTestRoom(1L, 101, 5000.0, 2, 4);
        room1.setCheckOutDate(LocalDate.now().plusDays(5));

        Room room2 = createTestRoom(2L, 505, 1000.0, 2, 4);
        room2.setCheckOutDate(LocalDate.now().plusDays(2));

        when(roomManager.getGuests(any(Comparator.class))).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms/occupied")
                        .param("sortBy", "date"))
                .andExpect(status().isOk());

        verify(roomManager).getGuests(comparatorCaptor.capture());
        assertTrue(comparatorCaptor.getValue().compare(room2, room1) < 0); // room2 < room1
    }

    @Test
    public void getOccupiedRooms_ReturnsBadRequest() throws Exception {
        String errorMessage = "Ошибка получения списка занятых комнат";

        when(roomManager.getGuests(any(Comparator.class))).thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/occupied"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void getCountGuests_Success() throws Exception {
        long expectedCount = 5L;
        when(roomManager.getCountGuests()).thenReturn(expectedCount);

        mockMvc.perform(get("/api/rooms/occupied/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(expectedCount));

        verify(roomManager, times(1)).getCountGuests();
    }

    @Test
    public void getCountGuests_ReturnsBadRequest() throws Exception {
        String errorMessage = "Не удалось подсчитать гостей";
        when(roomManager.getCountGuests()).thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/occupied/count"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }


    @Test
    public void getPaymentForRoom_Success() throws Exception {
        int roomNumber = 101;
        double expectedPayment = 15000.0;

        when(roomManager.getPaymentForRoom(roomNumber)).thenReturn(expectedPayment);

        mockMvc.perform(get("/api/rooms/{number}/payment", roomNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(expectedPayment));

        verify(roomManager, times(1)).getPaymentForRoom(roomNumber);
    }

    @Test
    public void getPaymentForRoom_RoomNotFound() throws Exception {
        int invalidRoomNumber = 999;
        String errorMessage = "Комната с номером " + invalidRoomNumber + " не найдена";

        when(roomManager.getPaymentForRoom(invalidRoomNumber))
                .thenThrow(new HotelException(errorMessage));

        mockMvc.perform(get("/api/rooms/{number}/payment", invalidRoomNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).getPaymentForRoom(invalidRoomNumber);
    }


    @Test
    public void addRoom_Success() throws Exception {
        int roomNumber = 202;
        double price = 2500.0;
        int capacity = 3;
        int stars = 4;

        task.dto.RoomDto roomDto = new task.dto.RoomDto();
        roomDto.setNumber(roomNumber);
        roomDto.setPrice(price);
        roomDto.setCapacity(capacity);
        roomDto.setStars(stars);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isCreated());


        verify(roomManager, times(1)).addRoom(roomNumber, price, capacity, stars);
    }

    @Test
    public void addRoom_RoomAlreadyExists() throws Exception {
        int roomNumber = 202;
        double price = 2500.0;
        int capacity = 3;
        int stars = 4;

        task.dto.RoomDto roomDto = new task.dto.RoomDto();
        roomDto.setNumber(roomNumber);
        roomDto.setPrice(price);
        roomDto.setCapacity(capacity);
        roomDto.setStars(stars);

        String errorMessage = "Комната с номером " + roomNumber + " уже существует";

        doThrow(new HotelException(errorMessage))
                .when(roomManager).addRoom(roomNumber, price, capacity, stars);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).addRoom(roomNumber, price, capacity, stars);
    }


    @Test
    public void updateRoomPrice_Success() throws Exception {
        int roomNumber = 101;
        Double newPrice = 2000.0;

        mockMvc.perform(put("/api/rooms/{number}/price", roomNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPrice)))
                .andExpect(status().isOk());

        verify(roomManager, times(1)).updatePriceRoom(roomNumber, newPrice);
    }

    @Test
    public void updateRoomPrice_RoomNotFound() throws Exception {
        int roomNumber = 999;
        Double newPrice = 2000.0;
        String errorMessage = "Комната " + roomNumber + " не найдена";

        doThrow(new HotelException(errorMessage))
                .when(roomManager).updatePriceRoom(roomNumber, newPrice);

        mockMvc.perform(put("/api/rooms/{number}/price", roomNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPrice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).updatePriceRoom(roomNumber, newPrice);
    }

    @Test
    public void updateRoomStatus_Success() throws Exception {
        int roomNumber = 101;
        RoomStatus newStatus = RoomStatus.values()[0];

        mockMvc.perform(put("/api/rooms/{number}/status", roomNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isOk());

        verify(roomManager, times(1)).setRoomStatus(roomNumber, newStatus);
    }

    @Test
    public void updateRoomStatus_RoomNotFound() throws Exception {
        int roomNumber = 999;
        RoomStatus newStatus = RoomStatus.values()[0];
        String errorMessage = "Комната " + roomNumber + " не найдена";

        doThrow(new HotelException(errorMessage))
                .when(roomManager).setRoomStatus(roomNumber, newStatus);

        mockMvc.perform(put("/api/rooms/{number}/status", roomNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roomManager, times(1)).setRoomStatus(roomNumber, newStatus);
    }
}

