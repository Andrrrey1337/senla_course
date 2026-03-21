package task.service.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dao.ServiceDao;
import task.dao.ServiceRecordDao;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Service;
import task.model.ServiceRecord;
import task.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRecordManagerTest {

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private GuestManager guestManager;
    @Mock
    private ServiceDao serviceDao;
    @Mock
    private ServiceRecordDao serviceRecordDao;

    @InjectMocks
    private ServiceRecordManager serviceRecordManager;

    @Test
    public void orderService_Success() {
        String guestName = "Иван Иванов";
        String serviceName = "Уборка";
        LocalDate date = LocalDate.now();
        Service service = new Service(1L, serviceName, 500.0);
        Guest guest = new Guest(1L, guestName);

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.of(service));
        when(guestManager.createOrFindGuest(guestName)).thenReturn(guest);
        when(idGenerator.next()).thenReturn(1L);

        serviceRecordManager.orderService(guestName, serviceName, date);

        verify(serviceRecordDao, times(1)).create(any(ServiceRecord.class));
    }

    @Test
    public void orderService_ServiceNotFound() {
        String guestName = "Иван Иванов";
        String serviceName = "Несуществующая услуга";
        LocalDate date = LocalDate.now();

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.empty());

        assertThrows(HotelException.class, () -> serviceRecordManager.orderService(guestName, serviceName, date));

        verify(guestManager, never()).createOrFindGuest(anyString());
        verify(serviceRecordDao, never()).create(any());
    }

    @Test
    public void getGuestServicesSortedByPrice_Success() {
        String guestName = "Иван Иванов";
        Guest guest = new Guest(1L, guestName);

        Service expensiveService = new Service(1L, "Дорогая", 2000.0);
        Service cheapService = new Service(2L, "Дешевая", 500.0);

        ServiceRecord record1 = new ServiceRecord(1L, guest.getId(), expensiveService.getId(), LocalDate.now());
        ServiceRecord record2 = new ServiceRecord(2L, guest.getId(), cheapService.getId(), LocalDate.now());

        when(guestManager.getGuestByName(guestName)).thenReturn(guest);
        when(serviceDao.findAll()).thenReturn(List.of(expensiveService, cheapService));
        when(serviceRecordDao.findByGuestId(guest.getId())).thenReturn(List.of(record1, record2));

        List<ServiceRecord> result = serviceRecordManager.getGuestServicesSortedByPrice(guestName);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getServiceId());
        assertEquals(1L, result.get(1).getServiceId());
    }

    @Test
    public void getGuestServicesSortedByDate_Success() {
        String guestName = "Иван Иванов";
        Guest guest = new Guest(1L, guestName);

        ServiceRecord record1 = new ServiceRecord(1L, guest.getId(), 1L, LocalDate.now().plusDays(2));
        ServiceRecord record2 = new ServiceRecord(2L, guest.getId(), 2L, LocalDate.now());

        when(guestManager.getGuestByName(guestName)).thenReturn(guest);
        when(serviceRecordDao.findByGuestId(guest.getId())).thenReturn(List.of(record1, record2));

        List<ServiceRecord> result = serviceRecordManager.getGuestServicesSortedByDate(guestName);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
    }

    @Test
    public void getAllRecords_Success() {
        when(serviceRecordDao.findAll()).thenReturn(List.of(new ServiceRecord(), new ServiceRecord()));

        List<ServiceRecord> result = serviceRecordManager.getAllRecords();

        assertEquals(2, result.size());
        verify(serviceRecordDao, times(1)).findAll();
    }

    @Test
    public void updateOrCreateRecord_UpdateSuccess() {
        long id = 1L;
        long guestId = 1L;
        long serviceId = 1L;
        LocalDate date = LocalDate.now();
        ServiceRecord existingRecord = new ServiceRecord(id, guestId, serviceId, date);

        when(serviceRecordDao.findById(id)).thenReturn(Optional.of(existingRecord));

        serviceRecordManager.updateOrCreateRecord(id, guestId, serviceId, date);

        verify(serviceRecordDao, times(1)).update(any(ServiceRecord.class));
        verify(serviceRecordDao, never()).create(any());
    }

    @Test
    public void updateOrCreateRecord_CreateSuccess() {
        long id = 1L;
        long guestId = 1L;
        long serviceId = 1L;
        LocalDate date = LocalDate.now();

        when(serviceRecordDao.findById(id)).thenReturn(Optional.empty());

        serviceRecordManager.updateOrCreateRecord(id, guestId, serviceId, date);

        verify(serviceRecordDao, times(1)).create(any(ServiceRecord.class));
        verify(serviceRecordDao, never()).update(any());
    }
}