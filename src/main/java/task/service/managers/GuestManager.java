package task.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.dao.GuestDao;
import task.exceptions.HotelException;
import task.model.Guest;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;

import java.util.List;
import java.util.Optional;

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

    public Guest createOrFindGuest(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.GUEST_NAME_EMPTY);
        }
        Optional<Guest> found = guestDao.findByName(name);
        if (found.isPresent()) {
            LOGGER.info("Найден существующий гость в базе: {}", name);
            return found.get();
        }

        Guest guest = new Guest(idGenerator.next(), name);
        LOGGER.info("Зарегистрирован новый гость: {}", name);
        return guestDao.create(guest);
    }

    public Guest getGuestByName(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException(BusinessMessages.GUEST_NAME_EMPTY);
        }
        return guestDao.findByName(name)
                .orElseThrow(() -> new HotelException(BusinessMessages.GUEST_NOT_FOUND_PREFIX + name + BusinessMessages.GUEST_NOT_FOUND_SUFFIX));
    }

    public Guest getGuestById(long id) throws HotelException {
        return guestDao.findById(id)
                .orElseThrow(() -> new HotelException("Гость с id=" + id + " не найден"));
    }

    public List<Guest> getAllGuests() {
        return guestDao.findAll();
    }

    // Методы для DataManager
    public void updateOrCreateGuest(long id, String name) throws HotelException {
        Optional<Guest> existing = guestDao.findById(id);
        if (existing.isPresent()) {
            Guest g = existing.get();
            g.setName(name);
            guestDao.update(g);
        } else {
            guestDao.create(new Guest(id, name));
        }
    }

    public  boolean guestExists(long id) {
            return guestDao.findById(id).isPresent();
    }
}
