package task_1_2_3.service.managers;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.annotations.Inject;
import task_1_2_3.dao.GuestDao;
import task_1_2_3.db.ConnectionManager;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.exceptions.HotelException;
import task_1_2_3.model.Guest;
import task_1_2_3.util.IdGenerator;
import task_1_2_3.util.constants.BusinessMessages;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Singleton
public class GuestManager {
    private static final Logger logger = LoggerFactory.getLogger(GuestManager.class);

    private static GuestManager instance;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private GuestDao guestDao;

    public GuestManager() {
        instance = this;
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

    public static Guest getGuestById(long id) throws HotelException {
        if (instance == null || instance.guestDao == null) {
            throw new HotelException("GuestManager не инициализирован");
        }
        try {
            return instance.guestDao.findById(id)
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

    public static boolean guestExists(long id) {
        if (instance == null || instance.guestDao == null) return false;
        try {
            return instance.guestDao.findById(id).isPresent();
        } catch (DaoException e) {
            logger.error("Ошибка проверки существования гостя id={}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
