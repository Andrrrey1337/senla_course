package task_1.service.managers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task_1.dao.ServiceDao;
import task_1.dao.ServiceRecordDao;
import task_1.db.ConnectionManager;
import task_1.exceptions.DaoException;
import task_1.exceptions.HotelException;
import task_1.model.Guest;
import task_1.model.Service;
import task_1.model.ServiceRecord;
import task_1.util.IdGenerator;
import task_1.util.constants.BusinessMessages;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ServiceRecordManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRecordManager.class);

    private final IdGenerator idGenerator;
    private final GuestManager guestManager;
    private final ServiceDao serviceDao;
    private final ServiceRecordDao serviceRecordDao;

    public ServiceRecordManager(IdGenerator idGenerator, GuestManager guestManager,
                                ServiceDao serviceDao, ServiceRecordDao serviceRecordDao) {
        this.idGenerator = idGenerator;
        this.guestManager = guestManager;
        this.serviceDao = serviceDao;
        this.serviceRecordDao = serviceRecordDao;
    }


    public void orderService(String guestName, String serviceName, LocalDate date) throws HotelException {
        ConnectionManager cm;
        try {
            ConnectionManager.getInstance().beginTransaction();

            Service service = serviceDao.findByName(serviceName)
                    .orElseThrow(() -> new HotelException(
                            BusinessMessages.SERVICE_NOT_FOUND_PREFIX + serviceName + BusinessMessages.SERVICE_NOT_FOUND_SUFFIX
                    ));

          Guest guest = guestManager.createOrFindGuest(guestName);

            ServiceRecord record = new ServiceRecord(idGenerator.next(), guest.getId(), service.getId(), date);
            serviceRecordDao.create(record);

            ConnectionManager.getInstance().commitTransaction();
        } catch (DaoException | HotelException e) {
            rollbackQuietly();
            throw (e instanceof HotelException) ? (HotelException) e : new HotelException(e.getMessage(), e);
        }
    }

    public List<ServiceRecord> getGuestServicesSortedByPrice(String guestName) throws HotelException {
        Guest guest = guestManager.getGuestByName(guestName);
        try {
            Map<Long, Double> priceByServiceId = new HashMap<>();
            for (Service s : serviceDao.findAll()) {
                priceByServiceId.put(s.getId(), s.getPrice());
            }

            return serviceRecordDao.findByGuestId(guest.getId()).stream()
                    .sorted(Comparator.comparingDouble(r -> priceByServiceId.getOrDefault(r.getServiceId(), 0.0)))
                    .toList();

        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<ServiceRecord> getGuestServicesSortedByDate(String guestName) throws HotelException {
        Guest guest = guestManager.getGuestByName(guestName);
        try {
            return serviceRecordDao.findByGuestId(guest.getId()).stream()
                    .sorted(Comparator.comparing(ServiceRecord::getDate))
                    .toList();
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<ServiceRecord> getAllRecords() throws HotelException {
        try {
            return serviceRecordDao.findAll();
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            logger.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }

    // методы для DataManager (CSV import)
    public void updateOrCreateRecord(long id, long guestId, long serviceId, LocalDate date) throws HotelException {
        try {
            ServiceRecord rec = new ServiceRecord(id, guestId, serviceId, date);
            if (serviceRecordDao.findById(id).isPresent()) {
                serviceRecordDao.update(rec);
            } else {
                serviceRecordDao.create(rec);
            }
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }
}
