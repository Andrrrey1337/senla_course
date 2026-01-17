package task_2_3_4.service.managers;

import task_2_3_4.annotations.Component;
import task_2_3_4.annotations.Singleton;
import task_2_3_4.annotations.Inject;
import task_2_3_4.dao.GuestDao;
import task_2_3_4.exceptions.DaoException;
import task_2_3_4.exceptions.HotelException;
import task_2_3_4.model.Guest;
import task_2_3_4.util.IdGenerator;
import task_2_3_4.util.constants.BusinessMessages;

import java.util.List;
import java.util.Optional;

@Component
@Singleton
public class GuestManager {

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
            Optional<Guest> found = guestDao.findByName(name);
            if (found.isPresent()) return found.get();

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

    public static boolean guestExists(long id) {
        if (instance == null || instance.guestDao == null) return false;
        try {
            return instance.guestDao.findById(id).isPresent();
        } catch (DaoException e) {
            return false;
        }
    }
}
