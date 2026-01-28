package task_1_2_3.service.managers;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.annotations.Inject;
import task_1_2_3.dao.ServiceDao;
import task_1_2_3.db.ConnectionManager;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.exceptions.HotelException;
import task_1_2_3.model.Service;
import task_1_2_3.util.IdGenerator;
import task_1_2_3.util.constants.BusinessMessages;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Singleton
public class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private static ServiceManager instance;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private ServiceDao serviceDao;

    public ServiceManager() {
        instance = this;
    }

    public void addService(String name, double price) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.SERVICE_NAME_EMPTY);
        }
        if (price < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            if (serviceDao.findByName(name).isPresent()) {
                throw new HotelException("Услуга с названием '" + name + "' уже существует");
            }
            serviceDao.create(new Service(idGenerator.next(), name, price));

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public void updatePriceService(String name, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException(BusinessMessages.PRICE_NEGATIVE);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            Service service = serviceDao.findByName(name)
                    .orElseThrow(() -> new HotelException(BusinessMessages.SERVICE_NOT_FOUND_PREFIX + name + BusinessMessages.SERVICE_NOT_FOUND_SUFFIX));

            service.setPrice(newPrice);
            serviceDao.update(service);

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public List<Service> getAllServices() {
        try {
            return serviceDao.findAll();
        } catch (DaoException e) {
            logger.error("Ошибка при получении списка услуг: {}", e.getMessage(), e);
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

    public static boolean serviceExists(long id) {
        if (instance == null || instance.serviceDao == null) return false;
        try {
            return instance.serviceDao.findById(id).isPresent();
        } catch (DaoException e) {
            logger.error("Ошибка проверки существования услуги id={}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            logger.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }
}
