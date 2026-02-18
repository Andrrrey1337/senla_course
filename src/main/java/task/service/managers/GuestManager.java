package task.service.managers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.dao.GuestDao;
import task.exceptions.DaoException;
import task.exceptions.HotelException;
import task.model.Guest;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class GuestManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestManager.class);

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
            Optional<Guest> found = guestDao.findByName(name);
            if (found.isPresent()) {
                return found.get();
            }

            Guest guest = new Guest(idGenerator.next(), name);
            return guestDao.create(guest);
        } catch (DaoException e) {
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
            LOGGER.error("Ошибка при получении списка гостей: {}", e.getMessage(), e);
            return List.of();
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
            LOGGER.error("Ошибка проверки существования гостя id={}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
