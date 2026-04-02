package task.service.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dao.ServiceDao;
import task.exceptions.HotelException;
import task.model.Service;
import task.util.IdGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceManagerTest {

    @Mock
    private ServiceDao serviceDao;
    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ServiceManager serviceManager;


    // addService
    @Test
    public void addService_Success() {
        // arrange
        String serviceName = "serviceName";
        double servicePrice = 100.00;

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.empty());
        when(idGenerator.next()).thenReturn(1L);

        // act
        serviceManager.addService(serviceName, servicePrice);

        // assert
        verify(serviceDao, times(1)).create(any(Service.class));
    }

    @Test
    public void addService_NameIsEmpty() {
        String serviceName = "";
        double servicePrice = 100.00;

        assertThrows(HotelException.class, () -> serviceManager.addService(serviceName, servicePrice));

        verify(serviceDao, never()).create(any(Service.class));
    }

    @Test
    public void addService_NameIsNull() {
        String serviceName = null;
        double servicePrice = 100.00;

        assertThrows(HotelException.class, () -> serviceManager.addService(serviceName, servicePrice));

        verify(serviceDao, never()).create(any(Service.class));
    }

    @Test
    public void addService_PriceIsNegative() {
        String serviceName = "serviceName";
        double servicePrice = -100.00;

        assertThrows(HotelException.class, () -> serviceManager.addService(serviceName, servicePrice));

        verify(serviceDao, never()).create(any(Service.class));
    }

    @Test
    public void addService_ServiceExists() {
        String serviceName = "serviceName";
        double servicePrice = 100.00;

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.of(new Service(1L, serviceName, servicePrice)));

        assertThrows(HotelException.class, () -> serviceManager.addService(serviceName, servicePrice));

        verify(serviceDao, never()).create(any(Service.class));
    }


    // updatePriceService
    @Test
    public void updatePriceService_Success() {
        String serviceName = "serviceName";
        double servicePrice = 100.00;
        Service service = new Service(1L, serviceName, servicePrice);

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.of(service));

        serviceManager.updatePriceService(serviceName, servicePrice);

        assertEquals(servicePrice, service.getPrice());

        verify(serviceDao, times(1)).update(service);
    }

    @Test
    public void updatePriceService_PriceIsNegative() {
        String serviceName = "serviceName";
        double servicePrice = -100.00;

        assertThrows(HotelException.class, () -> serviceManager.updatePriceService(serviceName, servicePrice));

        verify(serviceDao, never()).update(any(Service.class));
    }

    @Test
    public void updatePriceService_ServiceNotExists() {
        String serviceName = "serviceName";
        double servicePrice = 100.00;

        when(serviceDao.findByName(serviceName)).thenReturn(Optional.empty());

        assertThrows(HotelException.class, () -> serviceManager.updatePriceService(serviceName, servicePrice));

        verify(serviceDao, never()).update(any(Service.class));
    }


    //getAllServices
    @Test
    public void getAllServices_Success() {
        Service service1 = new Service(1L, "Услуга 1", 100.0);
        Service service2 = new Service(2L, "Услуга 2", 200.0);

        when(serviceDao.findAll()).thenReturn(List.of(service1, service2));

        List<Service> services = serviceManager.getAllServices();

        assertEquals(2, services.size());
        verify(serviceDao, times(1)).findAll();
    }


    //getAllServicesSortedByPrice
    @Test
    public void getAllServicesSortedByPrice_Success() {
        Service service1 = new Service(1L, "Дешевая", 100.0);
        Service service2 = new Service(2L, "Дорогая", 300.0);
        Service service3 = new Service(2L, "Средняя", 250.0);

        when(serviceDao.findAll()).thenReturn(List.of(service1, service2,  service3));

        List<Service> services = serviceManager.getAllServicesSortedByPrice();

        assertEquals("Дешевая", services.get(0).getName());
        assertEquals("Средняя", services.get(1).getName());
        assertEquals("Дорогая", services.get(2).getName());
    }


    //updateOrCreateService
    @Test
    public void updateOrCreateService_ServiceExists() {
        long serviceId = 1L;
        String serviceName = "serviceName";
        double servicePrice = 100.00;
        Service service = new Service(serviceId, serviceName, servicePrice);

        when(serviceDao.findById(serviceId)).thenReturn(Optional.of(service));

        serviceManager.updateOrCreateService(serviceId, serviceName, servicePrice);

        assertEquals(serviceName, service.getName());
        assertEquals(servicePrice, service.getPrice());

        verify(serviceDao, times(1)).findById(serviceId);
        verify(serviceDao, times(1)).update(service);
    }

    @Test
    public void updateOrCreateService_ServiceNotExists() {
        long serviceId = 1L;
        String serviceName = "serviceName";
        double servicePrice = 100.00;

        when(serviceDao.findById(serviceId)).thenReturn(Optional.empty());

        serviceManager.updateOrCreateService(serviceId, serviceName, servicePrice);

        verify(serviceDao, times(1)).findById(serviceId);
        verify(serviceDao, times(1)).create(any(Service.class));
    }


    //serviceExists
    @Test
    public void serviceExists_ServiceFound() {
        when(serviceDao.findById(1L)).thenReturn(Optional.of(new Service()));
        assertTrue(serviceManager.serviceExists(1L));
    }

    @Test
    void serviceExists_ServiceNotFound() {
        when(serviceDao.findById(99L)).thenReturn(Optional.empty());
        assertFalse(serviceManager.serviceExists(99L));
    }
}
