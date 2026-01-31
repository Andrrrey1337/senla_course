package task_1.service.managers;

import org.springframework.stereotype.Service;
import task_1.dao.GuestDao;
import task_1.db.ConnectionManager;
import task_1.exceptions.DaoException;
import task_1.exceptions.HotelException;
import task_1.model.Guest;
import task_1.util.IdGenerator;
import task_1.util.constants.BusinessMessages;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GuestManager {
    private static final Logger logger = LoggerFactory.getLogger(GuestManager.class);

    private final IdGenerator idGenerator;
    private final GuestDao guestDao;

    public GuestManager(IdGenerator idGenerator, GuestDao guestDao) {
        this.idGenerator = idGenerator;
        this.guestDao = guestDao;
    }

    Guest createOrFindGuest(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.GUEST_NAME_EMPTY);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            Optional<Guest> found = guestDao.findByName(name);
            if (found.isPresent()) {
                ConnectionManager.getInstance().commitTransaction();
                return found.get();
            }

            Guest guest = new Guest(idGenerator.next(), name);
            Guest created = guestDao.create(guest);

            ConnectionManager.getInstance().commitTransaction();
            return created;
        } catch (DaoException e) {
            rollbackQuietly();
            throw new HotelException(e.getMessage(), e);
        }
    }

    public Guest getGuestByName(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.GUEST_NAME_EMPTY);
        }
        try {
            return guestDao.findByName(name)
                    .orElseThrow(() -> new HotelException(BusinessMessages.GUEST_NOT_FOUND_PREFIX + name + BusinessMessages.GUEST_NOT_FOUND_SUFFIX));
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public Guest getGuestById(long id) throws HotelException {
        try {
            return guestDao.findById(id)
                    .orElseThrow(() -> new HotelException("Гость с id=" + id + " не найден"));
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<Guest> getAllGuests() {
        try {
            return guestDao.findAll();
        } catch (DaoException e) {
            logger.error("Ошибка при получении списка гостей: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            logger.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }

    // Методы для DataManager
    public void updateOrCreateGuest(long id, String name) throws HotelException {
        try {
            Optional<Guest> existing = guestDao.findById(id);
            if (existing.isPresent()) {
                Guest g = existing.get();
                g.setName(name);
                guestDao.update(g);
            } else {
                guestDao.create(new Guest(id, name));
            }
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public  boolean guestExists(long id) {
        try {
            return guestDao.findById(id).isPresent();
        } catch (DaoException e) {
            logger.error("Ошибка проверки существования гостя id={}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
