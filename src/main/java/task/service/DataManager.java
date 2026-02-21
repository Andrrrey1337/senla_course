package task.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.exceptions.HotelException;
import task.model.Guest;
import task.model.Room;
import task.model.RoomStatus;
import task.model.ServiceRecord;
import task.service.managers.GuestManager;
import task.service.managers.ResidenceManager;
import task.service.managers.RoomManager;
import task.service.managers.ServiceManager;
import task.service.managers.ServiceRecordManager;
import task.util.CsvManager;
import task.util.IdGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
    private final String dataPath;

    private final RoomManager roomManager;
    private final ServiceManager serviceManager;
    private final GuestManager guestManager;
    private final ServiceRecordManager serviceRecordManager;
    private final ResidenceManager residenceManager;
    private final IdGenerator idGeneratorState;

    public DataManager(@Value("${data.files.path}")String dataPath, RoomManager roomManager, ServiceManager serviceManager,
                       GuestManager guestManager, ServiceRecordManager serviceRecordManager,
                       ResidenceManager residenceManager, IdGenerator idGeneratorState) {
        this.dataPath = dataPath;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
        this.guestManager = guestManager;
        this.serviceRecordManager = serviceRecordManager;
        this.residenceManager = residenceManager;
        this.idGeneratorState = idGeneratorState;
    }

    public void exportGuests() throws HotelException {
        if (guestManager.getAllGuests().isEmpty()) {
            throw new HotelException("Невозможно экспортировать гостей: список гостей пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Guest guest : guestManager.getAllGuests()) {
            lines.add(guest.getId() + ";" + guest.getName());
        }
        try {
            CsvManager.write(dataPath + "importGuests.csv", lines);
            LOGGER.info("Экспорт гостей успешно завершен. Обработано записей: {}", lines.size());
        } catch (IOException e) {
            LOGGER.error("Критическая ошибка при экспорте гостей из файла", e);
            throw new HotelException("Ошибка при экспорте гостей в файл: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void importGuests() throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(dataPath + "importGuests.csv");
            long maxIdSeen = 0;
            for (List<String> cols : rows) {
                if (cols.size() < 2) continue;
                long id = Long.parseLong(cols.get(0));
                String name = cols.get(1);
                maxIdSeen = Math.max(maxIdSeen, id);
                guestManager.updateOrCreateGuest(id, name);
            }
            if (maxIdSeen > 0) idGeneratorState.setNext(maxIdSeen + 1);
            LOGGER.info("Импорт гостей успешно завершен. Обработано записей: {}", rows.size());
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка при импорте гостей из файла", e);
            throw new HotelException("Ошибка при импорте гостей из файла: " + e.getMessage(), e);
        }
    }

    public void exportServices() throws HotelException {
        if (serviceManager.getAllServices().isEmpty()) {
            throw new HotelException("Невозможно экспортировать услуги: список услуг пуст");
        }
        List<String> lines = new ArrayList<>();
        for (task.model.Service service : serviceManager.getAllServices()) {
            lines.add(service.getId() + ";" + service.getName() + ";" + service.getPrice());
        }
        try {
            CsvManager.write(dataPath + "importServices.csv", lines);
            LOGGER.info("Экспорт услуг успешно завершен. Обработано записей: {}", lines.size());
        } catch (IOException e) {
            LOGGER.error("Критическая ошибка при экспорте услуг из файла", e);
            throw new HotelException("Ошибка при экспорте услуг в файл: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void importServices() throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(dataPath + "importServices.csv");
            long maxId = 0;
            for (List<String> c : rows) {
                if (c.size() < 3) continue;
                long id = Long.parseLong(c.get(0));
                String name = c.get(1);
                double price = Double.parseDouble(c.get(2));
                maxId = Math.max(maxId, id);
                serviceManager.updateOrCreateService(id, name, price);
            }
            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
            LOGGER.info("Импорт услуг успешно завершен. Обработано записей: {}", rows.size());
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка при импорте услуг из файла", e);
            throw new HotelException("Ошибка при импорте услуг из файла: " + e.getMessage(), e);
        }
    }

    public void exportRooms() throws HotelException {
        if (roomManager.getAllRooms().isEmpty()) {
            throw new HotelException("Невозможно экспортировать комнаты: список комнат пуст");
        }
        List<String> lines = new ArrayList<>();
        for (Room room : roomManager.getAllRooms()) {
            long guestId = room.getGuest() != null ? room.getGuest().getId() : 0;
            lines.add(room.getId() + ";" + room.getNumber() + ";" + room.getPrice() + ";" + room.getCapacity() + ";" +
                    room.getStars() + ";" + room.getStatus() + ";" + guestId + ";" + room.getCheckInDate() + ";" + room.getCheckOutDate());
        }
        try {
            CsvManager.write(dataPath + "importRooms.csv", lines);
            LOGGER.info("Экспорт комнат успешно завершен. Обработано записей: {}", lines.size());
        } catch (IOException e) {
            LOGGER.error("Критическая ошибка при экспорте комнат из файла", e);
            throw new HotelException("Ошибка при экспорте комнат в файл: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void importRooms() throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(dataPath + "importRooms.csv");
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

                LocalDate checkInDate = (c.size() > 7 && c.get(7) != null && !c.get(7).isBlank()) ? LocalDate.parse(c.get(7)) : null;
                LocalDate checkOutDate = (c.size() > 8 && c.get(8) != null && !c.get(8).isBlank()) ? LocalDate.parse(c.get(8)) : null;

                maxId = Math.max(maxId, id);

                roomManager.updateOrCreateRoom(id, number, price, capacity, stars, status, guestId, checkInDate, checkOutDate);

                if (status == RoomStatus.OCCUPIED && guestId > 0 && checkInDate != null && checkOutDate != null) {
                    Room room = roomManager.getRoomDetails(number);
                    if (guestManager.guestExists(guestId)) {
                        residenceManager.createResidence(guestId, room.getId(), checkInDate, checkOutDate);
                    }
                }
            }

            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
            LOGGER.info("Импорт комнат успешно завершен. Обработано записей: {}", rows.size());
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка при импорте комнат из файла", e);
            throw new HotelException("Ошибка при импорте комнат из файла: " + e.getMessage(), e);
        }
    }

    public void exportServiceRecords() throws HotelException {
        if (serviceRecordManager.getAllRecords().isEmpty()) {
            throw new HotelException("Невозможно экспортировать записи услуг: список записей пуст");
        }
        List<String> lines = new ArrayList<>();
        for (ServiceRecord record : serviceRecordManager.getAllRecords()) {
            lines.add(record.getId() + ";" + record.getGuestId() + ";" + record.getServiceId() + ";" + record.getDate());
        }
        try {
            CsvManager.write(dataPath + "importGuests.csv", lines);
            LOGGER.info("Экспорт записей услуг успешно завершен. Обработано записей: {}", lines.size());
        } catch (IOException e) {
            LOGGER.error("Критическая ошибка при экспорте записей услуг из файла", e);
            throw new HotelException("Ошибка при экспорте записей услуг в файл: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void importServiceRecords() throws HotelException {
        try {
            List<List<String>> rows = CsvManager.read(dataPath + "importServiceRecords.csv");
            long maxId = 0;
            for (List<String> c : rows) {
                if (c.size() < 4) continue;

                long id = Long.parseLong(c.get(0));
                long guestId = Long.parseLong(c.get(1));
                long serviceId = Long.parseLong(c.get(2));
                LocalDate date = LocalDate.parse(c.get(3));

                maxId = Math.max(maxId, id);

                if (!guestManager.guestExists(guestId) || !serviceManager.serviceExists(serviceId)) {
                    continue;
                }
                serviceRecordManager.updateOrCreateRecord(id, guestId, serviceId, date);
            }
            if (maxId > 0) idGeneratorState.setNext(maxId + 1);
            LOGGER.info("Импорт записей услуг успешно завершен. Обработано записей: {}", rows.size());
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка при записей услуг комнат из файла", e);
            throw new HotelException("Ошибка при импорте записей услуг из файла: " + e.getMessage(), e);
        }
    }
}
