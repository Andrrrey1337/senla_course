package task_1.view;

import task_1.model.*;
import task_1.exceptions.HotelException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Builder {
    private static Scanner scanner = new Scanner(System.in);
    private Admin admin = Admin.getInstance();

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
                new MenuItem("Последние 3 гостя номера", this::printThreeLastGuests),
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
        System.out.print("Введите номер: ");
        int number = getIntInput();
        System.out.print("Введите цену за ночь: ");
        double price = getDoubleInput();
        System.out.print("Введите вместимость: ");
        int capacity = getIntInput();
        System.out.print("Введите количество звезд: ");
        int stars = getIntInput();

        try {
            admin.addRoom(number, price, capacity, stars);
            System.out.println("Комната с номером: " + number + " и стоимостью: " + price + " добавлена");
        } catch (HotelException e) {
            System.out.println("Ошибка при добавлении комнаты: " + e.getMessage());
        }
    }

    private void addService() {
        System.out.print("Введите название услуги: ");
        String name = scanner.nextLine();
        System.out.print("Введите цену: ");
        double price = getDoubleInput();

        try {
            admin.addService(name, price);
            System.out.println("Добавлена услуга с названием: " + name + " и стоимостью: " + price);
        } catch (HotelException e) {
            System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
        }
    }

    private void checkIn() {
        try {
            System.out.print("Введите номер комнаты: ");
            int number = getIntInput();
            System.out.print("Введите имя гостя: ");
            String name = scanner.nextLine();
            System.out.print("Введите дату заселения (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(scanner.nextLine());
            System.out.print("Введите дату выселения (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine());

            admin.checkIn(number, name, checkIn, checkOut);
            System.out.println("Гость: " + name + " заселен в номер: " + number);

        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты. Используйте YYYY-MM-DD");
        } catch (HotelException e) {
            System.out.println("Ошибка при заселении: " + e.getMessage());
        }
    }

    private void checkOut() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();

        try {
            admin.checkOut(number);
            System.out.println("Гость выселен из номера: " + number);
        } catch (HotelException e) {
            System.out.println("Ошибка при выселении: " + e.getMessage());
        }
    }

    private void orderService() {
        System.out.println("Введите имя заказчика: ");
        String name = scanner.nextLine();
        System.out.println("Введите название услуги: ");
        String service = scanner.nextLine();
        System.out.println("Введите дату (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        try {
            admin.orderService(name, service, date);
            System.out.println("Услуга " + service + " успешно заказана");
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты. Используйте YYYY-MM-DD.");
        } catch (HotelException e) {
            System.out.println("Ошибка при заказе услуги: " + e.getMessage());
        }
    }

    private void setRoomStatus() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        System.out.println("Выберите статус: 1 - AVAILABLE, 2 - OCCUPIED, 3 - REPAIR");
        int choice = getIntInput();
        RoomStatus status;
        if (choice == 1) {
            status = RoomStatus.AVAILABLE;
        } else if (choice == 2) {
            status = RoomStatus.OCCUPIED;
        } else if (choice == 3) {
            status = RoomStatus.REPAIR;
        } else {
            System.out.println("Неверный выбор");
            return;
        }

        try {
            admin.setRoomStatus(number, status);
            System.out.println("Статус номера: " + number + " изменен на: " + status);
        } catch (HotelException e) {
            System.out.println("Ошибка при изменении статуса комнаты: " + e.getMessage());
        }
    }

    private void updatePriceRoom() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        System.out.print("Введите новую цену: ");
        double price = getDoubleInput();

        try {
            admin.updatePriceRoom(number, price);
            System.out.println("Стоимость номера " + number + " изменена на: " + price);
        } catch (HotelException e) {
            System.out.println("Ошибка при обновлении цены комнаты: " + e.getMessage());
        }
    }

    private void updatePriceService() {
        System.out.print("Введите название услуги: ");
        String name = scanner.nextLine();
        System.out.print("Введите новую цену: ");
        double price = getDoubleInput();

        try {
            admin.updatePriceService(name, price);
            System.out.println("Стоимость услуги: " + name + " изменена на: " + price);
        } catch (HotelException e) {
            System.out.println("Ошибка при обновлении цены услуги: " + e.getMessage());
        }
    }

    private void printAllRooms() {
        System.out.println("Все номера в отеле:");
        for (Room room : admin.getAllRooms()) {
            System.out.println(room);
        }
    }

    private void printAllServices() {
        System.out.println("Все предоставляемые услуги:");
        for (Service service : admin.getAllServices()) {
            System.out.println(service);
        }
    }

    private void printSortedRoomsByPrice() {
        System.out.println("Номера отсортированы по цене:");
        for (Room room : admin.getSortedRooms(Comparator.comparingDouble(Room::getPrice))) {
            System.out.println(room);
        }
    }

    private void printSortedRoomsByCapacity() {
        System.out.println("Номера отсортированы вместительности:");
        for (Room room : admin.getSortedRooms(Comparator.comparingInt(Room::getCapacity))) {
            System.out.println(room);
        }
    }

    private void printSortedRoomsByStars() {
        System.out.println("Номера отсортированы по звездам:");
        for (Room room : admin.getSortedRooms(Comparator.comparingInt(Room::getStars))) {
            System.out.println(room);
        }
    }

    private void printAvailableSortedRoomsByPrice() {
        System.out.println("Свободные номера(отсортированы по цене):");
        for (Room room : admin.getAvailableRooms(Comparator.comparingDouble(Room::getPrice))) {
            System.out.println(room);
        }
    }

    private void printAvailableSortedRoomsByCapacity() {
        System.out.println("Свободные номера(отсортированы по вместительности):");
        for (Room room : admin.getAvailableRooms(Comparator.comparingInt(Room::getCapacity))) {
            System.out.println(room);
        }
    }

    private void printAvailableSortedRoomsByStars() {
        System.out.println("Свободные номера(отсортированы по звездам):");
        for (Room room : admin.getAvailableRooms(Comparator.comparingInt(Room::getStars))) {
            System.out.println(room);
        }
    }

    private void printRoomAvailableByDate() {
        try {
            System.out.print("Введите дату (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.println("Номера свободные к " + date + ":");
            for (Room room : admin.getRoomAvailableByDate(date)) {
                System.out.println("Номер: " + room.getNumber());
            }
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты. Используйте YYYY-MM-DD.");
        }
    }

    private void printGuestsByAlphabet() {
        System.out.println("Список постояльцев(отсортирован по алфавиту):");
        for (Room room : admin.getGuests(Comparator.comparing(room -> room.getGuest().getName()))) {
            System.out.println(room.getGuest().getName() + " выселится " + room.getCheckOutDate() + " из номера " + room.getNumber());
        }
    }

    private void printGuestsByCheckOutDate() {
        System.out.println("Список постояльцев(отсортирован по дате выселения):");
        for (Room room : admin.getGuests(Comparator.comparing(Room::getCheckOutDate))) {
            System.out.println(room.getGuest().getName() + " выселится " + room.getCheckOutDate() + " из номера " + room.getNumber());
        }
    }

    private void printCountAvailableRooms() {
        long count = admin.getCountAvailableRooms();
        System.out.println("Всего свободных номеров: " + count);
    }

    private void printCountGuests() {
        long count = admin.getCountGuests();
        System.out.println("Всего жильцов: " + count);
    }

    private void printGuestServicesByPrice() {
        System.out.print("Введите имя гостя: ");
        String name = scanner.nextLine();
        try {
            List<ServiceRecord> records = admin.getGuestServicesSortedByPrice(name);
            if (records.isEmpty()) {
                System.out.println("У гостя " + name + " нет заказанных услуг");
                return;
            }
            System.out.println("Список услуг постояльца " + name + ", отсортированный по цене:");
            for (ServiceRecord record : records) {
                Service service = admin.getAllServices().stream()
                        .filter(s -> s.getId() == record.getServiceId())
                        .findFirst()
                        .orElse(null);
                if (service != null) {
                    System.out.println("Услуга " + service.getName() + " за " + service.getPrice() + " от " + record.getDate());
                }
            }
        } catch (HotelException e) {
            System.out.println("Ошибка при получении услуг гостя: " + e.getMessage());
        }
    }

    private void printGuestServicesByDate() {
        System.out.print("Введите имя гостя: ");
        String name = scanner.nextLine();
        try {
            List<ServiceRecord> records = admin.getGuestServicesSortedByDate(name);
            if (records.isEmpty()) {
                System.out.println("У гостя " + name + " нет заказанных услуг");
                return;
            }
            System.out.println("Список услуг постояльца " + name + ", отсортированный по дате:");
            for (ServiceRecord record : records) {
                Service service = admin.getAllServices().stream()
                        .filter(s -> s.getId() == record.getServiceId())
                        .findFirst()
                        .orElse(null);
                if (service != null) {
                    System.out.println("Услуга " + service.getName() + " за " + service.getPrice() + " от " + record.getDate());
                }
            }
        } catch (HotelException e) {
            System.out.println("Ошибка при получении услуг гостя: " + e.getMessage());
        }
    }

    private void printPaymentForRoom() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        double payment = admin.getPaymentForRoom(number);
        System.out.println("Сумма к оплате за номер " + number + ": " + payment);
    }

    private void printThreeLastGuests() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        List<Residence> history = admin.getThreeLastGuests(number);
        System.out.println("Последние 3 постояльца номера " + number + ":");
        for (Residence residence : history) {
            System.out.println(residence.getGuest());
        }
    }

    private void printAllPrices() {
        System.out.println("Цены на номера:");
        for (Room room : admin.getAllRoomsSortedByPrice()) {
            System.out.println("Номер " + room.getNumber() + " стоит " + room.getPrice());
        }
        System.out.println("Цены услуг:");
        for (Service service : admin.getAllServicesSortedByPrice()) {
            System.out.println("Услуга " + service.getName() + " стоит " + service.getPrice());
        }
    }

    private void printRoomDetails() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        try {
            Room room = admin.getRoomDetails(number);
            System.out.println(room);
        } catch (HotelException e) {
            System.out.println("Ошибка при получении деталей комнаты: " + e.getMessage());
        }
    }

    private void exportGuests() {
        System.out.print("Введите путь файла CSV для экспорта данных о гостях: ");
        String path = scanner.nextLine();
        try {
            admin.exportGuests(path);
            System.out.println("Данные о гостях успешно экспортированы в " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
        }
    }

    private void importGuests() {
        System.out.print("Введите путь файла CSV для импорта данных о гостях: ");
        String path = scanner.nextLine();
        try {
            admin.importGuests(path);
            System.out.println("Данные о гостях успешно импортированы из " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при импорте гостей: " + e.getMessage());
        }
    }

    private void exportServices() {
        System.out.print("Введите путь файла CSV для экспорта данных об услугах: ");
        String path = scanner.nextLine();
        try {
            admin.exportServices(path);
            System.out.println("Данные об услугах успешно экспортированы в " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }

    private void importServices() {
        System.out.print("Введите путь файла CSV для импорта данных об услугах: ");
        String path = scanner.nextLine();
        try {
            admin.importServices(path);
            System.out.println("Данные об услугах успешно импортированы из " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    private void exportRooms() {
        System.out.print("Введите путь файла CSV для экспорта данных о номерах: ");
        String path = scanner.nextLine();
        try {
            admin.exportRooms(path);
            System.out.println("Данные о номерах успешно экспортированы в " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при экспорте номеров: " + e.getMessage());
        }
    }

    private void importRooms() {
        System.out.print("Введите путь файла CSV для импорта данных о номерах: ");
        String path = scanner.nextLine();
        try {
            admin.importRooms(path);
            System.out.println("Данные о номерах успешно импортированы из " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при импорте номеров: " + e.getMessage());
        }
    }

    private void exportServiceRecords() {
        System.out.print("Введите путь файла CSV для экспорта данных о записях услуг: ");
        String path = scanner.nextLine();
        try {
            admin.exportServiceRecords(path);
            System.out.println("Данные о записях услуг успешно экспортированы в " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при экспорте записей услуг: " + e.getMessage());
        }
    }

    private void importServiceRecords() {
        System.out.print("Введите путь файла CSV для импорта данных о записях услуг: ");
        String path = scanner.nextLine();
        try {
            admin.importServiceRecords(path);
            System.out.println("Данные о записях услуг успешно импортированы из " + path);
        } catch (HotelException e) {
            System.out.println("Ошибка при импорте записей услуг: " + e.getMessage());
        }
    }

    // вспомогательные методы ввода, тк после nextInt может остаться в буфере
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }
}