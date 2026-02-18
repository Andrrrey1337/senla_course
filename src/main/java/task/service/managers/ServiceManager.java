package task.service.managers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import task.dao.ServiceDao;
import task.exceptions.DaoException;
import task.exceptions.HotelException;
import task.model.Service;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Transactional
@org.springframework.stereotype.Service
public class ServiceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);

    private final IdGenerator idGenerator;
    private final ServiceDao serviceDao;

    public ServiceManager(IdGenerator idGenerator, ServiceDao serviceDao) {
        this.idGenerator = idGenerator;
        this.serviceDao = serviceDao;
    }

    public void addService(String name, double price) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.SERVICE_NAME_EMPTY);
        }
        if (price < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            if (serviceDao.findByName(name).isPresent()) {
                throw new HotelException("Услуга с названием '" + name + "' уже существует");
            }
            serviceDao.create(new Service(idGenerator.next(), name, price));
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public void updatePriceService(String name, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            Service service = serviceDao.findByName(name)
                    .orElseThrow(() -> new HotelException(BusinessMessages.SERVICE_NOT_FOUND_PREFIX + name + BusinessMessages.SERVICE_NOT_FOUND_SUFFIX));

            service.setPrice(newPrice);
            serviceDao.update(service);
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<Service> getAllServices() {
        try {
            return serviceDao.findAll();
        } catch (DaoException e) {
            LOGGER.error("Ошибка при получении списка услуг: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<Service> getAllServicesSortedByPrice() {
        return getAllServices().stream()
                .sorted(Comparator.comparing(Service::getPrice))
                .toList();
    }

    // методы для DataManager
    public void updateOrCreateService(long id, String name, double price) throws HotelException {
        try {
            Optional<Service> existing = serviceDao.findById(id);
            if (existing.isPresent()) {
                Service s = existing.get();
                s.setName(name);
                s.setPrice(price);
                serviceDao.update(s);
            } else {
                serviceDao.create(new Service(id, name, price));
            }
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public  boolean serviceExists(long id) {
        try {
            return serviceDao.findById(id).isPresent();
        } catch (DaoException e) {
            LOGGER.error("Ошибка проверки существования услуги id={}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
