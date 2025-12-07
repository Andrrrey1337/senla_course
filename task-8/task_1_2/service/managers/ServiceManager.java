package service.managers;

import annotations.Component;
import annotations.Singleton;
import annotations.Inject;
import exceptions.HotelException;
import model.Service;
import util.IdGenerator;
import java.io.Serializable;
import java.util.*;

@Component
@Singleton
public class ServiceManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Service> servicesByName = new HashMap<>();
    private final Map<Long, Service> servicesById = new HashMap<>();

    @Inject
    private IdGenerator idGenerator;

    public void addService(String name, double price) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException("Название услуги не может быть пустым");
        }
        if (price < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        if (servicesByName.containsKey(name)) {
            throw new HotelException("Услуга с названием '" + name + "' уже существует");
        }
        Service service = new Service(idGenerator.next(), name, price);
        servicesByName.put(name, service);
        servicesById.put(service.getId(), service);
    }

    public void updatePriceService(String name, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        Service service = servicesByName.get(name);
        if (service == null) {
            throw new HotelException("Услуга с названием '" + name + "' не найдена");
        }
        service.setPrice(newPrice);
    }

    public List<Service> getAllServices() {
        return new ArrayList<>(servicesByName.values());
    }

    public List<Service> getAllServicesSortedByPrice() {
        return servicesByName.values().stream()
                .sorted(Comparator.comparing(Service::getPrice))
                .toList();
    }

    //  методы для DataManager
    public void updateOrCreateService(long id, String name, double price) {
        if (servicesById.containsKey(id)) {
            Service ex = servicesById.get(id);
            ex.setName(name);
            ex.setPrice(price);
            servicesByName.remove(ex.getName());
            servicesByName.put(name, ex);
        } else {
            Service service = new Service(id, name, price);
            servicesById.put(id, service);
            servicesByName.put(name, service);
        }
    }

    public boolean serviceExists(long id) {
        return servicesById.containsKey(id);
    }
}