package task.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.service.managers.GuestManager;
import task.service.managers.ResidenceManager;
import task.service.managers.RoomManager;
import task.service.managers.ServiceManager;
import task.service.managers.ServiceRecordManager;
import task.util.CsvManager;
import task.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataManagerTest {

    @Mock
    private RoomManager roomManager;
    @Mock
    private ServiceManager serviceManager;
    @Mock
    private GuestManager guestManager;
    @Mock
    private ServiceRecordManager serviceRecordManager;
    @Mock
    private ResidenceManager residenceManager;
    @Mock
    private IdGenerator idGeneratorState;

    private DataManager dataManager;
    private final String dataPath = "test/path/";

    @BeforeEach
    public void setUp() {
        dataManager = new DataManager(
                dataPath, roomManager, serviceManager, guestManager,
                serviceRecordManager, residenceManager, idGeneratorState
        );
    }

    @Test
    public void exportGuests_Success() {
        Guest guest = new Guest(1L, "Иван");

        when(guestManager.getAllGuests()).thenReturn(List.of(guest));

        // так как мы используем статические методы, то нам нужен статический  мок
        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            dataManager.exportGuests();

            csvMock.verify(() -> CsvManager.write(eq(dataPath + "importGuests.csv"), anyList()), times(1));
        }
    }

    @Test
    public void exportGuests_EmptyList() {
        when(guestManager.getAllGuests()).thenReturn(List.of());

        assertThrows(HotelException.class, () -> dataManager.exportGuests());
    }

    @Test
    public void importGuests_Success() {
        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            csvMock.when(() -> CsvManager.read(dataPath + "importGuests.csv"))
                    .thenReturn(List.of(List.of("1", "Иван Иванов")));

            dataManager.importGuests();

            verify(guestManager, times(1)).updateOrCreateGuest(1L, "Иван Иванов");
            verify(idGeneratorState, times(1)).setNext(2L);
        }
    }

    @Test
    public void exportServices_Success() {
        task.model.Service service = new task.model.Service(1L, "Завтрак", 500.0);

        when(serviceManager.getAllServices()).thenReturn(List.of(service));

        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            dataManager.exportServices();

            csvMock.verify(() -> CsvManager.write(eq(dataPath + "importServices.csv"), anyList()), times(1));
        }
    }

    @Test
    public void exportServices_EmptyListThrowsException() {
        when(serviceManager.getAllServices()).thenReturn(List.of());

        assertThrows(HotelException.class, () -> dataManager.exportServices());
    }

    @Test
    public void importServices_Success() {
        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            csvMock.when(() -> CsvManager.read(dataPath + "importServices.csv"))
                    .thenReturn(List.of(List.of("1", "Уборка", "300.0")));

            dataManager.importServices();

            verify(serviceManager, times(1)).updateOrCreateService(1L, "Уборка", 300.0);
            verify(idGeneratorState, times(1)).setNext(2L);
        }
    }

    @Test
    public void exportRooms_Success() {
        Room room = new Room(1L, 101, 1000.0, 2, 3);

        when(roomManager.getAllRooms()).thenReturn(List.of(room));

        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            dataManager.exportRooms();

            csvMock.verify(() -> CsvManager.write(eq(dataPath + "importRooms.csv"), anyList()), times(1));
        }
    }

    @Test
    public void exportRooms_EmptyListThrowsException() {
        when(roomManager.getAllRooms()).thenReturn(List.of());

        assertThrows(HotelException.class, () -> dataManager.exportRooms());
    }

    @Test
    public void importRooms_SuccessWithResidenceCreation() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        List<String> row = List.of(
                "1", "101", "1000.0", "2", "3", "OCCUPIED", "5", checkIn.toString(), checkOut.toString()
        );

        Room room = new Room(1L, 101, 1000.0, 2, 3);

        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            csvMock.when(() -> CsvManager.read(dataPath + "importRooms.csv")).thenReturn(List.of(row));

            when(roomManager.getRoomDetails(101)).thenReturn(room);
            when(guestManager.guestExists(5L)).thenReturn(true);

            dataManager.importRooms();

            verify(roomManager, times(1)).updateOrCreateRoom(
                    1L, 101, 1000.0, 2, 3, RoomStatus.OCCUPIED, 5L, checkIn, checkOut
            );
            verify(residenceManager, times(1)).createResidence(5L, 1L, checkIn, checkOut);
            verify(idGeneratorState, times(1)).setNext(2L);
        }
    }

    @Test
    public void importRooms_ValidationException() {
        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            csvMock.when(() -> CsvManager.read(dataPath + "importRooms.csv")).thenThrow(new RuntimeException("File not found"));

            assertThrows(RuntimeException.class, () -> dataManager.importRooms());
        }
    }

    @Test
    public void exportServiceRecords_Success() {
        task.model.ServiceRecord record = new task.model.ServiceRecord(1L, 1L, 1L, LocalDate.now());

        when(serviceRecordManager.getAllRecords()).thenReturn(List.of(record));

        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            dataManager.exportServiceRecords();

            csvMock.verify(() -> CsvManager.write(eq(dataPath + "importServiceRecords.csv"), anyList()), times(1));
        }
    }

    @Test
    public void exportServiceRecords_EmptyListThrowsException() {
        when(serviceRecordManager.getAllRecords()).thenReturn(List.of());

        assertThrows(HotelException.class, () -> dataManager.exportServiceRecords());
    }

    @Test
    public void importServiceRecords_Success() {
        LocalDate date = LocalDate.now();

        try (MockedStatic<CsvManager> csvMock = mockStatic(CsvManager.class)) {
            csvMock.when(() -> CsvManager.read(dataPath + "importServiceRecords.csv"))
                    .thenReturn(List.of(List.of("1", "2", "3", date.toString())));

            when(guestManager.guestExists(2L)).thenReturn(true);
            when(serviceManager.serviceExists(3L)).thenReturn(true);

            dataManager.importServiceRecords();

            verify(serviceRecordManager, times(1)).updateOrCreateRecord(1L, 2L, 3L, date);
            verify(idGeneratorState, times(1)).setNext(2L);
        }
    }
}