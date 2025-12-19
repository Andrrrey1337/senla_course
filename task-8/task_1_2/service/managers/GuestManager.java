package service.managers;

import annotations.Component;
import annotations.Singleton;
import annotations.Inject;
import exceptions.HotelException;
import model.Guest;
import util.IdGenerator;
import java.io.Serializable;
import java.util.*;

@Component
@Singleton
public class GuestManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Guest> guestsByName = new HashMap<>();
    private static final Map<Long, Guest> guestsById = new HashMap<>();

    @Inject
    private IdGenerator idGenerator;

    // остальной код без изменений
    Guest createOrFindGuest(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException("Имя гостя не может быть пустым");
        }
        if (guestsByName.containsKey(name)) {
            return guestsByName.get(name);
        }
        Guest guest = new Guest(idGenerator.next(), name);
        guestsByName.put(name, guest);
        guestsById.put(guest.getId(), guest);
        return guest;
    }

    public Guest getGuestByName(String name) throws HotelException {
        Guest guest = guestsByName.get(name);
        if (guest == null) {
            throw new HotelException("Гость с именем '" + name + "' не найден");
        }
        return guest;
    }

    public static Guest getGuestById(long id) throws HotelException {
        Guest guest = guestsById.get(id);
        if (guest == null) {
            throw new HotelException("Гость с id '" + id + "' не найден");
        }
        return guest;
    }

    public List<Guest> getAllGuests() {
        return new ArrayList<>(guestsById.values());
    }

    public void updateOrCreateGuest(long id, String name) {
        if (guestsById.containsKey(id)) {
            Guest existing = guestsById.get(id);
            String oldName = existing.getName();
            existing.setName(name);
            guestsByName.remove(oldName);
            guestsByName.put(name, existing);
        } else {
            Guest g = new Guest(id, name);
            guestsById.put(id, g);
            guestsByName.put(name, g);
        }
    }

    public static boolean guestExists(long id) {
        return guestsById.containsKey(id);
    }
}