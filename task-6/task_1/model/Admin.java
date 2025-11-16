package task_1.model;

import task_1.IdGenerator.IdGenerator;
import task_1.csv.CsvManager;
import task_1.exceptions.HotelException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Admin {
    private static Admin instance;
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<String, Service> servicesByName = new HashMap<>();
    private final Map<Long, Service> servicesById = new HashMap<>();
    private final Map<Long, List<ServiceRecord>> serviceRecordsByGuestId = new HashMap<>();
    private final Map<String, Guest> guestsByName = new HashMap<>();
    private final Map<Long, Guest> guestsById = new HashMap<>();

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    private Guest createOrFindGuest(String name) throws HotelException {
        if (name == null || name.trim().isEmpty()) {
            throw new HotelException("Имя гостя не может быть пустым");
        }
        if (guestsByName.containsKey(name)){
            return guestsByName.get(name);
        }
        Guest guest = new Guest(name);
        guestsByName.put(name,guest);
        guestsById.put(guest.getId(), guest);
        return guest;
    }

    public void orderService(String guestName, String serviceName, LocalDate date) throws HotelException {
        if (!servicesByName.containsKey(serviceName)) {
            throw new HotelException("Услуга с названием '" + serviceName + "' не найдена");
        }
        Guest guest = createOrFindGuest(guestName);
        List<ServiceRecord> records = serviceRecordsByGuestId.get(guest.getId());
        if (records == null) {
            records = new ArrayList<>();
            serviceRecordsByGuestId.put(guest.getId(), records);
        }
        records.add(new ServiceRecord(guestsByName.get(guestName).getId(), servicesByName.get(serviceName).getId(), date));
    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new HotelException("Комната с номером " + number + " недоступна для заселения (статус: " + room.getStatus() + ")");
        }
        if (checkInDate.isAfter(checkOutDate)) {
            throw new HotelException("Дата заселения не может быть позже даты выселения");
        }
        Guest guest = createOrFindGuest(guestName);
        room.setGuest(guest);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setCheckInDate(checkInDate);
        room.setCheckOutDate(checkOutDate);
        room.addResidence(guest,checkInDate,checkOutDate);
    }

    public void checkOut(int number) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            throw new HotelException("Комната с номером " + number + " не занята, невозможно выполнить выселение");
        }
        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckOutDate(null);
    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        room.setStatus(status);
    }

    public void updatePriceRoom(int number, double newPrice) throws HotelException {
        if (newPrice < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        room.setPrice(newPrice);
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

    public void addRoom(int number, double price, int capacity, int stars) throws HotelException {
        if (price < 0 || capacity <= 0 || stars <= 0) {
            throw new HotelException("Цена, вместимость и количество звезд должны быть положительными");
        }
        if (rooms.containsKey(number)) {
            throw new HotelException("Комната с номером " + number + " уже существует");
        }
        rooms.put(number, new Room(number, price, capacity, stars));
    }

    public void addService(String name, double price) throws HotelException {
        if (name == null || name.trim().isEmpty()) {
            throw new HotelException("Название услуги не может быть пустым");
        }
        if (price < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        if (servicesByName.containsKey(name)) {
            throw new HotelException("Услуга с названием '" + name + "' уже существует");
        }
        Service service = new Service(name, price);
        servicesByName.put(name, service);
        servicesById.put(service.getId(), service);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Service> getAllServices() {
        return new ArrayList<>(servicesByName.values());
    }

    public List<Room> getSortedRooms(Comparator<Room> comparator) {
        return rooms.values().stream()
                .sorted(comparator)
                .toList();
    }

    public List<Room> getAvailableRooms(Comparator<Room> comparator) {
        return rooms.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .sorted(comparator)
                .toList();
    }

    public List<Room> getRoomAvailableByDate(LocalDate date) {
        return rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE ||
                        (room.getCheckOutDate() != null && date.isAfter(room.getCheckOutDate())))
                .toList();
    }

    public List<Room> getGuests(Comparator<Room> comparator) {
        return rooms.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                .sorted(comparator)
                .toList();
    }

    public long getCountAvailableRooms() {
        return rooms.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count();
    }

    public long getCountGuests() {
        return rooms.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                .count();
    }

    public double getPaymentForRoom(int number) {
        Room room = rooms.get(number);
        return room.calculatePayment();
    }

    public List<Residence> getThreeLastGuests(int number) {
        Room room = rooms.get(number);
        return room.getResidence();
    }

    public List<ServiceRecord> getGuestServicesSortedByPrice(String guestName) throws HotelException {
        Guest guest = guestsByName.get(guestName);
        if (guest == null) {
            throw new HotelException("Гость с именем '" + guestName + "' не найден");
        }
        if (!serviceRecordsByGuestId.containsKey(guest.getId())) {
            return Collections.emptyList();
        }
        return serviceRecordsByGuestId.get(guest.getId()).stream()
                .sorted(Comparator.comparingDouble(record -> servicesById.get(record.getServiceId()).getPrice()))
                .toList();
    }

    public List<ServiceRecord> getGuestServicesSortedByDate(String guestName) throws HotelException {
        Guest guest = guestsByName.get(guestName);
        if (guest == null) {
            throw new HotelException("Гость с именем '" + guestName + "' не найден");
        }
        if (!serviceRecordsByGuestId.containsKey(guest.getId())) {
            return Collections.emptyList();
        }
        return serviceRecordsByGuestId.get(guest.getId()).stream()
                .sorted(Comparator.comparing(ServiceRecord::getDate))
                .toList();
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
    }

    public List<Service> getAllServicesSortedByPrice() {
        return servicesByName.values().stream()
                .sorted(Comparator.comparing(Service::getPrice))
                .toList();
    }

    public Room getRoomDetails(int number) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        return room;
    }

    public void exportGuests(String filePath) throws HotelException {
        List<String> lines = new ArrayList<>();
        for (Guest guest : guestsById.values()) {
            lines.add(guest.getId() + ";" + guest.getName());
        }
        CsvManager.write(filePath, lines);
    }

    public void importGuests(String filePath) throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(filePath);
            long maxIdSeen = 0;
            for (List<String> cols : rows) {
                if (cols.size() < 2) continue;
                long id = Long.parseLong(cols.get(0));
                String name = cols.get(1);
                maxIdSeen = Math.max(maxIdSeen, id);
                if (guestsById.containsKey(id)) {
                    // обновляем имя
                    Guest existing = guestsById.get(id);
                    String oldName = existing.getName();
                    existing.setName(name);
                    // обновим индекс по имени
                    guestsByName.remove(oldName);
                    guestsByName.put(name, existing);
                } else {
                    Guest g = new Guest(id, name);
                    guestsById.put(id, g);
                    guestsByName.put(name, g);
                }
            }
            // чтобы новые автоматически сгенерированные id не конфликтовали с импортированными
            if (maxIdSeen > 0) IdGenerator.setNext(maxIdSeen + 1);
        } catch (NumberFormatException e) {
            throw new HotelException("Ошибка при импорте гостей из файла: " + e.getMessage(), e);
        }
    }

    public void exportServices(String path) throws HotelException {
        List<String> lines = new ArrayList<>();
        for (Service s : servicesById.values()) {
            lines.add(s.getId() + ";" + s.getName() + ";" + s.getPrice());
        }
        CsvManager.write(path, lines);
    }

    public void importServices(String path) throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(path);
            long maxId = 0;
            for (List<String> c : rows) {
                if (c.size() < 3) continue;
                long id = Long.parseLong(c.get(0));
                String name = c.get(1);
                double price = Double.parseDouble(c.get(2));
                maxId = Math.max(maxId, id);
                if (servicesById.containsKey(id)) {
                    Service ex = servicesById.get(id);
                    // обновляем
                    ex.setName(name);
                    ex.setPrice(price);
                    // обновим индекс по имени
                    servicesByName.remove(ex.getName());
                    servicesByName.put(name, ex);
                } else {
                    Service s = new Service(id, name, price);
                    servicesById.put(id, s);
                    servicesByName.put(name, s);
                }
            }
            if (maxId > 0) IdGenerator.setNext(maxId + 1);
        } catch (NumberFormatException e) {
            throw new HotelException("Ошибка при импорте услуг из файла: " + e.getMessage(), e);
        }
    }

    public void exportRooms(String path) throws HotelException {
        List<String> lines = new ArrayList<>();
        for (Room r : rooms.values()) {
            long guestId = r.getGuest() != null ? r.getGuest().getId() : 0;
            lines.add(r.getId() + ";" + r.getNumber() + ";" + r.getPrice() + ";" + r.getCapacity() + ";" +
                    r.getStars() + ";" + r.getStatus() + ";" + guestId);
        }
        CsvManager.write(path, lines);
    }

    public void importRooms(String path) throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(path);
            long maxId = 0;
            for (List<String> c : rows) {
                if (c.size() < 7) continue;
                long id = Long.parseLong(c.get(0));
                int number = Integer.parseInt(c.get(1));
                double price = Double.parseDouble(c.get(2));
                int capacity = Integer.parseInt(c.get(3));
                int stars = Integer.parseInt(c.get(4));
                RoomStatus status = RoomStatus.valueOf(c.get(5));
                long guestId = Long.parseLong(c.get(6));
                maxId = Math.max(maxId, id);
                // найти комнату с таким id
                Room foundById = rooms.values().stream()
                        .filter(room -> room.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (foundById != null) {
                    int oldNumber = foundById.getNumber();
                    foundById.setNumber(number);
                    foundById.setPrice(price);
                    foundById.setCapacity(capacity);
                    foundById.setStars(stars);
                    foundById.setStatus(status);
                    if (guestsById.containsKey(guestId)) {
                        foundById.setGuest(guestsById.get(guestId));
                    } else {
                        foundById.setGuest(null);
                    }
                    if (oldNumber != number) {
                        rooms.put(number, foundById);
                        rooms.remove(oldNumber);
                    }
                } else {
                    // если номер занят другой комнатой
                    if (rooms.containsKey(number)) {
                        // если номер уже существует, обновим существующую комнату
                        Room rExist = rooms.get(number);
                        rExist.setPrice(price);
                        rExist.setCapacity(capacity);
                        rExist.setStars(stars);
                        rExist.setStatus(status);
                        if (guestsById.containsKey(guestId)) {
                            rExist.setGuest(guestsById.get(guestId));
                        }
                    } else {
                        // создать новую комнату с указанным id
                        Room r = new Room(id, number, price, capacity, stars, status);
                        if (guestsById.containsKey(guestId)) {
                            r.setGuest(guestsById.get(guestId));
                        }
                        rooms.put(number, r);
                    }
                }
            }
            if (maxId > 0) IdGenerator.setNext(maxId + 1);
        } catch (IllegalArgumentException e) {
            throw new HotelException("Ошибка при импорте комнат из файла: " + e.getMessage(), e);
        }
    }

    public void exportServiceRecords(String path) throws HotelException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<Long, List<ServiceRecord>> e : serviceRecordsByGuestId.entrySet()) {
            for (ServiceRecord r : e.getValue()) {
                lines.add(r.getId() + ";" + r.getGuestId() + ";" + r.getServiceId() + ";" + r.getDate());
            }
        }
        CsvManager.write(path, lines);
    }

    public void importServiceRecords(String path) throws HotelException {
        try {
            List<List<String >> rows = CsvManager.read(path);
            long maxId = 0;
            for (List<String> c : rows) {
                if (c.size() < 4) continue;
                long id = Long.parseLong(c.get(0));
                long guestId = Long.parseLong(c.get(1));
                long serviceId = Long.parseLong(c.get(2));
                LocalDate date = LocalDate.parse(c.get(3));
                maxId = Math.max(maxId, id);
                if (!guestsById.containsKey(guestId) || !servicesById.containsKey(serviceId)) {
                    System.out.println("Пропущена запись услуги: guestId=" + guestId + " или serviceId=" + serviceId + " не найдены");
                    continue;
                }
                // находим запись с нужным id по id клиента
                List<ServiceRecord> list = serviceRecordsByGuestId.computeIfAbsent(guestId, l -> new ArrayList<>());
                ServiceRecord exists = list.stream()
                        .filter(r -> r.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (exists != null) {
                    list.remove(exists);
                }
                ServiceRecord rec = new ServiceRecord(id, guestId, serviceId, date);
                list.add(rec);
            }
            if (maxId > 0) IdGenerator.setNext(maxId + 1);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new HotelException("Ошибка при импорте записей услуг из файла: " + e.getMessage(), e);
        }
    }
}