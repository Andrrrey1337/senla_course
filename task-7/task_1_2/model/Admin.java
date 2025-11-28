package task_1_2.model;

import task_1_2.config.ConfigManager;
import task_1_2.util.IdGenerator;
import task_1_2.util.CsvManager;
import task_1_2.exceptions.HotelException;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Admin implements Serializable {
    private static Admin instance;
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<String, Service> servicesByName = new HashMap<>();
    private final Map<Long, Service> servicesById = new HashMap<>();
    private final Map<Long, List<ServiceRecord>> serviceRecordsByGuestId = new HashMap<>();
    private final Map<String, Guest> guestsByName = new HashMap<>();
    private final Map<Long, Guest> guestsById = new HashMap<>();
    private final IdGenerator idGeneratorState = new IdGenerator();

    private Admin() {}

    public static void setInstanceForLoading(Admin loadedAdmin) {
        if (instance == null) {
            instance = loadedAdmin;
        }
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    private Guest createOrFindGuest(String name) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException("Имя гостя не может быть пустым");
        }
        if (guestsByName.containsKey(name)){
            return guestsByName.get(name);
        }
        Guest guest = new Guest(idGeneratorState.next(), name);
        guestsByName.put(name,guest);
        guestsById.put(guest.getId(), guest);
        return guest;
    }

    public void orderService(String guestName, String serviceName, LocalDate date) throws HotelException {
        if (!servicesByName.containsKey(serviceName)) {
            throw new HotelException("Услуга с названием '" + serviceName + "' не найдена");
        }
        Guest guest = createOrFindGuest(guestName);
        List<ServiceRecord> records = serviceRecordsByGuestId.computeIfAbsent(guest.getId(), k -> new ArrayList<>());
        records.add(new ServiceRecord(idGeneratorState.next(), guest.getId(), servicesByName.get(serviceName).getId(), date));

    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        Room room = rooms.get(number);
        if (room == null) {
            throw new HotelException("Комната с номером " + number + " не найдена");
        }
        if (RoomStatus.AVAILABLE != room.getStatus()) {
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
        if (RoomStatus.OCCUPIED != room.getStatus()) {
            throw new HotelException("Комната с номером " + number + " не занята, невозможно выполнить выселение");
        }
        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckOutDate(null);
    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        try {
            if (!ConfigManager.getInstance().isRoomStatusChangeEnabled()) {
                throw new HotelException("Возможность изменять статус номера отключена в конфигурации.");
            }
        } catch (IOException e) { // Обработка возможной ошибки при загрузке конфига
            throw new HotelException("Ошибка при загрузке конфигурации: " + e.getMessage(), e);
        }
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
        rooms.put(number, new Room(idGeneratorState.next(), number, price, capacity, stars));
    }

    public void addService(String name, double price) throws HotelException {
        if (name == null || name.isEmpty()) {
            throw new HotelException("Название услуги не может быть пустым");
        }
        if (price < 0) {
            throw new HotelException("Цена не может быть отрицательной");
        }
        if (servicesByName.containsKey(name)) {
            throw new HotelException("Услуга с названием '" + name + "' уже существует");
        }
        Service service = new Service(idGeneratorState.next(), name, price);
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
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public List<Room> getRoomAvailableByDate(LocalDate date) {
        return rooms.values().stream()
                .filter(room -> RoomStatus.AVAILABLE == room.getStatus() ||
                        (room.getCheckOutDate() != null && date.isAfter(room.getCheckOutDate())))
                .toList();
    }

    public List<Room> getGuests(Comparator<Room> comparator) {
        return rooms.values().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
                .sorted(comparator)
                .toList();
    }

    public long getCountAvailableRooms() {
        return rooms.values().stream()
                .filter(room ->RoomStatus.AVAILABLE == room.getStatus())
                .count();
    }

    public long getCountGuests() {
        return rooms.values().stream()
                .filter(room -> RoomStatus.OCCUPIED == room.getStatus())
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
        if (guestsById.isEmpty()) {
            throw new HotelException("Невозможно экспортировать гостей: список гостей пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Guest guest : guestsById.values()) {
            lines.add(guest.getId() + ";" + guest.getName());
        }
        try {
            CsvManager.write(filePath, lines);
        } catch (IOException e) {
            throw new HotelException("Ошибка при экспорте гостей в файл: " + e.getMessage(), e);
        }
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
            if (maxIdSeen > 0) idGeneratorState.setNext(maxIdSeen + 1);
        } catch (NumberFormatException | IOException e) {
            throw new HotelException("Ошибка при импорте гостей из файла: " + e.getMessage(), e);
        }
    }

    public void exportServices(String path) throws HotelException {
        if (servicesById.isEmpty()) {
            throw new HotelException("Невозможно экспортировать услуги: список услуг пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Service service : servicesById.values()) {
            lines.add(service.getId() + ";" + service.getName() + ";" + service.getPrice());
        }
        try {
            CsvManager.write(path, lines);
        } catch (IOException e) {
            throw new HotelException("Ошибка при экспорте услуг в файл: " + e.getMessage(), e);
        }
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
                    Service service = new Service(id, name, price);
                    servicesById.put(id, service);
                    servicesByName.put(name, service);
                }
            }
            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
        } catch (NumberFormatException | IOException e) {
            throw new HotelException("Ошибка при импорте услуг из файла: " + e.getMessage(), e);
        }
    }

    public void exportRooms(String path) throws HotelException {
        if (rooms.isEmpty()) {
            throw new HotelException("Невозможно экспортировать комнаты: список комнат пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Room room : rooms.values()) {
            long guestId = room.getGuest() != null ? room.getGuest().getId() : 0;
            lines.add(room.getId() + ";" + room.getNumber() + ";" + room.getPrice() + ";" + room.getCapacity() + ";" +
                    room.getStars() + ";" + room.getStatus() + ";" + guestId + ";" + room.getCheckInDate() + ";" + room.getCheckOutDate());
        }
        try {
            CsvManager.write(path, lines);
        } catch (IOException e) {
            throw new HotelException("Ошибка при экспорте комнат в файл: " + e.getMessage(), e);
        }
    }
    private void updateExistingRoomById(Room foundById, long id,int number,double price,int capacity,int stars,
                                        RoomStatus status, long guestId, LocalDate checkInDate,LocalDate checkOutDate) {
        int oldNumber = foundById.getNumber();
        foundById.setNumber(number);
        foundById.setPrice(price);
        foundById.setCapacity(capacity);
        foundById.setStars(stars);
        foundById.setStatus(status);
        foundById.setCheckInDate(checkInDate);
        foundById.setCheckOutDate(checkOutDate);
        foundById.setGuest(guestsById.getOrDefault(guestId, null));
        if (oldNumber != number) {
            rooms.put(number, foundById);
            rooms.remove(oldNumber);
        }
    }

    private void updateOrCreateRoomByNumber( long id,int number,double price,int capacity,int stars,
                                             RoomStatus status, long guestId, LocalDate checkInDate,LocalDate checkOutDate) {
        // если номер занят другой комнатой
        if (rooms.containsKey(number)) {
            // если номер уже существует, обновим существующую комнату
            Room rExist = rooms.get(number);
            rExist.setPrice(price);
            rExist.setCapacity(capacity);
            rExist.setStars(stars);
            rExist.setStatus(status);
            rExist.setCheckInDate(checkInDate);
            rExist.setCheckOutDate(checkOutDate);
            if (guestsById.containsKey(guestId)) {
                rExist.setGuest(guestsById.get(guestId));
            }
        } else {
            // создать новую комнату с указанным id
            Room room = new Room(id, number, price, capacity, stars, status, checkInDate, checkOutDate);
            if (guestsById.containsKey(guestId)) {
                room.setGuest(guestsById.get(guestId));
            }
            rooms.put(number, room);
        }
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
                LocalDate checkInDate = c.size() > 7 ? LocalDate.parse(c.get(7)) : null;
                LocalDate checkOutDate = c.size() > 7 ? LocalDate.parse(c.get(8)) : null;

                maxId = Math.max(maxId, id);
                // найти комнату с таким id
                Room foundById = rooms.values().stream()
                        .filter(room -> room.getId() == id)
                        .findFirst()
                        .orElse(null);

                if (foundById != null) {
                    updateExistingRoomById(foundById, id, number, price, capacity, stars, status,
                            guestId, checkInDate, checkOutDate);
                } else {
                    updateOrCreateRoomByNumber(id, number, price, capacity, stars, status, guestId,
                            checkInDate, checkOutDate);
                }
            }

            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
        } catch (IllegalArgumentException | IOException e) {
            throw new HotelException("Ошибка при импорте комнат из файла: " + e.getMessage(), e);
        }
    }

    public void exportServiceRecords(String path) throws HotelException {
        if (serviceRecordsByGuestId.isEmpty()) {
            throw new HotelException("Невозможно экспортировать записи услуг: список записей пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Map.Entry<Long, List<ServiceRecord>> e : serviceRecordsByGuestId.entrySet()) {
            for (ServiceRecord record : e.getValue()) {
                lines.add(record.getId() + ";" + record.getGuestId() + ";" + record.getServiceId() + ";" + record.getDate());
            }
        }
        try {
            CsvManager.write(path, lines);
        } catch (IOException e) {
            throw new HotelException("Ошибка при экспорте записей услуг в файл: " + e.getMessage(), e);
        }
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
                serviceRecordsByGuestId.get(guestId).add(rec);
            }
            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
        } catch (NumberFormatException | DateTimeParseException | IOException e) {
            throw new HotelException("Ошибка при импорте записей услуг из файла: " + e.getMessage(), e);
        }
    }
}