package task_1.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import task_1.exceptions.HotelException;
import task_1.model.*;
import task_1.service.HotelService;
import task_1.service.managers.GuestManager;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class Builder {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    private final HotelService hotelService;
    private final GuestManager guestManager;
    private final ConsoleUI ui;

    public Builder(HotelService hotelService, GuestManager guestManager, ConsoleUI ui) {
        this.hotelService = hotelService;
        this.guestManager = guestManager;
        this.ui = ui;
    }

    public Menu buildRootMenu() {
        return new Menu("Главное меню",
                new MenuItem("Добавить номер", this::addRoom),
                new MenuItem("Добавить услугу", this::addService),
                new MenuItem("Заселить гостя", this::checkIn),
                new MenuItem("Выселить гостя", this::checkOut),
                new MenuItem("Заказать услугу", this::orderService),
                new MenuItem("Изменить статус номера", this::setRoomStatus),
                new MenuItem("Обновить цену номера", this::updatePriceRoom),
                new MenuItem("Обновить цену услуги", this::updatePriceService),
                new MenuItem("Просмотреть все номера", this::printAllRooms),
                new MenuItem("Просмотреть все услуги", this::printAllServices),
                new MenuItem("Просмотреть номера (по цене)", this::printSortedRoomsByPrice),
                new MenuItem("Просмотреть номера (по вместительности)", this::printSortedRoomsByCapacity),
                new MenuItem("Просмотреть номера (по звездам)", this::printSortedRoomsByStars),
                new MenuItem("Просмотреть свободные номера (по цене)", this::printAvailableSortedRoomsByPrice),
                new MenuItem("Просмотреть свободные номера (по вместительности)", this::printAvailableSortedRoomsByCapacity),
                new MenuItem("Просмотреть свободные номера (по звездам)", this::printAvailableSortedRoomsByStars),
                new MenuItem("Просмотреть свободные номера к определенной дате", this::printRoomAvailableByDate),
                new MenuItem("Просмотреть постояльцев (по алфавиту)", this::printGuestsByAlphabet),
                new MenuItem("Просмотреть постояльцев (по дате выселения)", this::printGuestsByCheckOutDate),
                new MenuItem("Просмотреть услуги гостя (по цене)", this::printGuestServicesByPrice),
                new MenuItem("Просмотреть услуги гостя (по дате)", this::printGuestServicesByDate),
                new MenuItem("Посмотреть кол-во свободных номеров", this::printCountAvailableRooms),
                new MenuItem("Посмотреть кол-во постояльцев", this::printCountGuests),
                new MenuItem("Оплата за номер", this::printPaymentForRoom),
                new MenuItem("Последние N гостей номера", this::printThreeLastGuests),
                new MenuItem("Все цены", this::printAllPrices),
                new MenuItem("Детали номера", this::printRoomDetails),
                new MenuItem("Экспорт гостей (CSV)", this::exportGuests),
                new MenuItem("Импорт гостей (CSV)", this::importGuests),
                new MenuItem("Экспорт услуг (CSV)", this::exportServices),
                new MenuItem("Импорт услуг (CSV)", this::importServices),
                new MenuItem("Экспорт комнат (CSV)", this::exportRooms),
                new MenuItem("Импорт комнат (CSV)", this::importRooms),
                new MenuItem("Экспорт записей услуг (CSV)", this::exportServiceRecords),
                new MenuItem("Импорт записей услуг (CSV)", this::importServiceRecords),
                new MenuItem("Выход", () -> System.exit(0))
        );
    }

    private void addRoom() {
        int number = ui.readInt("Введите номер");
        double price = ui.readDouble("Введите цену за ночь");
        int capacity = ui.readInt("Введите вместимость");
        int stars = ui.readInt("Введите количество звезд");

        try {
            hotelService.addRoom(number, price, capacity, stars);
            ui.print("Комната с номером: " + number + " и стоимостью: " + price + " добавлена");
        } catch (HotelException e) {
            handleException("Ошибка при добавлении номера " + number, e);
        }
    }

    private void addService() {
        String name = ui.readString("Введите название услуги");
        double price = ui.readDouble("Введите цену");

        try {
            hotelService.addService(name, price);
            ui.print("Добавлена услуга с названием: " + name + " и стоимостью: " + price);
        } catch (HotelException e) {
            handleException("Ошибка при добавлении услуги '" + name + "'", e);
        }
    }

    private void checkIn() {
        int number = ui.readInt("Введите номер комнаты");
        String name = ui.readString("Введите имя гостя");
        LocalDate checkIn = ui.readDate("Введите дату заселения");
        LocalDate checkOut = ui.readDate("Введите дату выселения");

        try {
            hotelService.checkIn(number, name, checkIn, checkOut);
            ui.print("Гость: " + name + " заселен в номер: " + number);
        } catch (HotelException e) {
            handleException("Ошибка при заселении гостя '" + name + "' в номер " + number, e);
        }
    }

    private void checkOut() {
        int number = ui.readInt("Введите номер комнаты");
        try {
            hotelService.checkOut(number);
            ui.print("Гость выселен из номера: " + number);
        } catch (HotelException e) {
            handleException("Ошибка при выселении из номера " + number, e);
        }
    }

    private void orderService() {
        String name = ui.readString("Введите имя заказчика");
        String service = ui.readString("Введите название услуги");
        LocalDate date = ui.readDate("Введите дату");

        try {
            hotelService.orderService(name, service, date);
            ui.print("Услуга " + service + " успешно заказана");
        } catch (HotelException e) {
            handleException("Ошибка при заказе услуги '" + service + "' для гостя '" + name + "'", e);
        }
    }

    private void setRoomStatus() {
        int number = ui.readInt("Введите номер комнаты");
        int choice = ui.readInt("Выберите статус: 1 - AVAILABLE, 2 - OCCUPIED, 3 - REPAIR");

        RoomStatus status;
        if (choice == 1) status = RoomStatus.AVAILABLE;
        else if (choice == 2) status = RoomStatus.OCCUPIED;
        else if (choice == 3) status = RoomStatus.REPAIR;
        else {
            ui.printError("Неверный выбор статуса");
            return;
        }

        try {
            hotelService.setRoomStatus(number, status);
            ui.print("Статус номера: " + number + " изменен на: " + status);
        } catch (HotelException e) {
            handleException("Ошибка при изменении статуса номера " + number, e);
        }
    }

    private void updatePriceRoom() {
        int number = ui.readInt("Введите номер комнаты");
        double price = ui.readDouble("Введите новую цену");

        try {
            hotelService.updatePriceRoom(number, price);
            ui.print("Стоимость номера " + number + " изменена на: " + price);
        } catch (HotelException e) {
            handleException("Ошибка при обновлении цены номера " + number, e);
        }
    }

    private void updatePriceService() {
        String name = ui.readString("Введите название услуги");
        double price = ui.readDouble("Введите новую цену");

        try {
            hotelService.updatePriceService(name, price);
            ui.print("Стоимость услуги: " + name + " изменена на: " + price);
        } catch (HotelException e) {
            handleException("Ошибка при обновлении цены услуги '" + name + "'", e);
        }
    }

    private void printAllRooms() {
        ui.print("Все номера в отеле:");
        hotelService.getAllRooms().forEach(room -> ui.print(room.toString()));
    }

    private void printAllServices() {
        ui.print("Все предоставляемые услуги:");
        hotelService.getAllServices().forEach(service -> ui.print(service.toString()));
    }

    private void printSortedRoomsByPrice() {
        ui.print("Номера отсортированы по цене:");
        hotelService.getSortedRooms(Comparator.comparingDouble(Room::getPrice))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printSortedRoomsByCapacity() {
        ui.print("Номера отсортированы вместительности:");
        hotelService.getSortedRooms(Comparator.comparingInt(Room::getCapacity))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printSortedRoomsByStars() {
        ui.print("Номера отсортированы по звездам:");
        hotelService.getSortedRooms(Comparator.comparingInt(Room::getStars))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printAvailableSortedRoomsByPrice() {
        ui.print("Свободные номера (отсортированы по цене):");
        hotelService.getAvailableRooms(Comparator.comparingDouble(Room::getPrice))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printAvailableSortedRoomsByCapacity() {
        ui.print("Свободные номера (отсортированы по вместительности):");
        hotelService.getAvailableRooms(Comparator.comparingInt(Room::getCapacity))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printAvailableSortedRoomsByStars() {
        ui.print("Свободные номера (отсортированы по звездам):");
        hotelService.getAvailableRooms(Comparator.comparingInt(Room::getStars))
                .forEach(room -> ui.print(room.toString()));
    }

    private void printRoomAvailableByDate() {
        LocalDate date = ui.readDate("Введите дату");
        ui.print("Номера свободные к " + date + ":");
        hotelService.getRoomAvailableByDate(date)
                .forEach(room -> ui.print("Номер: " + room.getNumber()));
    }

    private void printGuestsByAlphabet() {
        ui.print("Список постояльцев (отсортирован по алфавиту):");
        hotelService.getGuests(Comparator.comparing(room -> room.getGuest().getName()))
                .forEach(room -> ui.print(room.getGuest().getName() + " выселится " + room.getCheckOutDate() + " из номера " + room.getNumber()));
    }

    private void printGuestsByCheckOutDate() {
        ui.print("Список постояльцев (отсортирован по дате выселения):");
        hotelService.getGuests(Comparator.comparing(Room::getCheckOutDate))
                .forEach(room -> ui.print(room.getGuest().getName() + " выселится " + room.getCheckOutDate() + " из номера " + room.getNumber()));
    }

    private void printCountAvailableRooms() {
        ui.print("Всего свободных номеров: " + hotelService.getCountAvailableRooms());
    }

    private void printCountGuests() {
        ui.print("Всего жильцов: " + hotelService.getCountGuests());
    }

    private void printGuestServicesByPrice() {
        String name = ui.readString("Введите имя гостя");
        try {
            List<ServiceRecord> records = hotelService.getGuestServicesSortedByPrice(name);
            if (records.isEmpty()) {
                ui.print("У гостя " + name + " нет заказанных услуг");
                return;
            }
            ui.print("Список услуг постояльца " + name + ", отсортированный по цене:");
            printServiceRecords(records);
        } catch (HotelException e) {
            handleException("Ошибка при получении услуг гостя", e);
        }
    }

    private void printGuestServicesByDate() {
        String name = ui.readString("Введите имя гостя");
        try {
            List<ServiceRecord> records = hotelService.getGuestServicesSortedByDate(name);
            if (records.isEmpty()) {
                ui.print("У гостя " + name + " нет заказанных услуг");
                return;
            }
            ui.print("Список услуг постояльца " + name + ", отсортированный по дате:");
            printServiceRecords(records);
        } catch (HotelException e) {
            handleException("Ошибка при получении услуг гостя", e);
        }
    }

    private void printServiceRecords(List<ServiceRecord> records) {
        for (ServiceRecord record : records) {
            Service service = hotelService.getAllServices().stream()
                    .filter(s -> s.getId() == record.getServiceId())
                    .findFirst()
                    .orElse(null);
            if (service != null) {
                ui.print("Услуга " + service.getName() + " за " + service.getPrice() + " от " + record.getDate());
            }
        }
    }

    private void printPaymentForRoom() {
        int number = ui.readInt("Введите номер комнаты");
        double payment = hotelService.getPaymentForRoom(number);
        ui.print("Сумма к оплате за номер " + number + ": " + payment);
    }

    private void printThreeLastGuests() {
        int number = ui.readInt("Введите номер комнаты");
        try {
            List<Residence> history = hotelService.getThreeLastGuests(number);
            ui.print("Последние 3 постояльца номера " + number + ":");
            for (Residence residence : history) {
                // Используем внедренный guestManager вместо статики
                try {
                    Guest guest = guestManager.getGuestById(residence.getGuestId());
                    ui.print(guest.getName());
                } catch (Exception e) {
                    ui.print("Гость ID=" + residence.getGuestId() + " (удален)");
                }
            }
        } catch (HotelException e) {
            handleException("Ошибка при получении истории", e);
        }
    }

    private void printAllPrices() {
        ui.print("Цены на номера:");
        hotelService.getAllRoomsSortedByPrice().forEach(r -> ui.print("Номер " + r.getNumber() + " стоит " + r.getPrice()));

        ui.print("Цены услуг:");
        hotelService.getAllServicesSortedByPrice().forEach(s -> ui.print("Услуга " + s.getName() + " стоит " + s.getPrice()));
    }

    private void printRoomDetails() {
        int number = ui.readInt("Введите номер комнаты");
        try {
            ui.print(hotelService.getRoomDetails(number).toString());
        } catch (HotelException e) {
            handleException("Ошибка при получении деталей номера", e);
        }
    }

    private void exportGuests() {
        String path = ui.readString("Введите путь файла CSV для экспорта гостей");
        try {
            hotelService.exportGuests(path);
            ui.print("Экспорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка экспорта", e);
        }
    }

    private void importGuests() {
        String path = ui.readString("Введите путь файла CSV для импорта гостей");
        try {
            hotelService.importGuests(path);
            ui.print("Импорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка импорта", e);
        }
    }

    private void exportServices() {
        String path = ui.readString("Введите путь файла CSV для экспорта услуг");
        try {
            hotelService.exportServices(path);
            ui.print("Экспорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка экспорта", e);
        }
    }

    private void importServices() {
        String path = ui.readString("Введите путь файла CSV для импорта услуг");
        try {
            hotelService.importServices(path);
            ui.print("Импорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка импорта", e);
        }
    }

    private void exportRooms() {
        String path = ui.readString("Введите путь файла CSV для экспорта номеров");
        try {
            hotelService.exportRooms(path);
            ui.print("Экспорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка экспорта", e);
        }
    }

    private void importRooms() {
        String path = ui.readString("Введите путь файла CSV для импорта номеров");
        try {
            hotelService.importRooms(path);
            ui.print("Импорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка импорта", e);
        }
    }

    private void exportServiceRecords() {
        String path = ui.readString("Введите путь файла CSV для экспорта записей");
        try {
            hotelService.exportServiceRecords(path);
            ui.print("Экспорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка экспорта", e);
        }
    }

    private void importServiceRecords() {
        String path = ui.readString("Введите путь файла CSV для импорта записей");
        try {
            hotelService.importServiceRecords(path);
            ui.print("Импорт успешно завершен");
        } catch (HotelException e) {
            handleException("Ошибка импорта", e);
        }
    }

    private void handleException(String msg, Exception e) {
        logger.error("{}: {}", msg, e.getMessage(), e);
        ui.printError(msg + ": " + e.getMessage());
    }
}