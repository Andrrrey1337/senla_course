package task_1.view;

import task_1.model.*;

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
                new MenuItem("Оплата за номер", this::printPaymentForRoom),
                new MenuItem("Последние 3 гостя номера", this::printThreeLastGuests),
                new MenuItem("Все цены", this::printAllPrices),
                new MenuItem("Детали номера", this::printRoomDetails),
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
        if (admin.addRoom(number, price, capacity, stars)) {
            System.out.println("Комната с номером: " + number + " и стоимостью: " + price + " добавлена");
        } else {
            System.out.println("Комната с таким номером уже существует: " + number);
        }
    }

    private void addService() {
        System.out.print("Введите название услуги: ");
        String name = scanner.nextLine();
        System.out.print("Введите цену: ");
        double price = getDoubleInput();
        if (admin.addService(name, price)) {
            System.out.println("Добавлена услуга с названием: " + name + " и стоимостью: " + price);
        } else {
            System.out.println("Услуга с таким названием уже существует: " + name);
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
            if (admin.checkIn(number, name, checkIn, checkOut)) {
                System.out.println("Гость: " + name + " заселен в номер: " + number);
            } else {
                System.out.println("Номер не найден или недоступен");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты");
        }

    }

    private void checkOut() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        if (admin.checkOut(number)) {
            System.out.println("Гость выселен из номера: " + number);
        } else {
            System.out.println("Комната итак свободна или не найдена");
        }
    }

    private void orderService() {
        System.out.println("Введите имя заказчика: ");
        String name = scanner.nextLine();
        System.out.println("Введите название услуги: ");
        String service = scanner.nextLine();
        System.out.println("Введите дату (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        if (admin.orderService(name, service, date)) {
            System.out.println("Услуга " + service + " успешно заказана");
        } else {
            System.out.println("Услуга не найдена");
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
            status = null;
        }
        if (status != null && admin.setRoomStatus(number, status)) {
            System.out.println("Статус номера: " + number + " изменен на: " + status);
        } else {
            System.out.println("Номер не найден");
        }
    }

    private void updatePriceRoom() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        System.out.print("Введите новую цену: ");
        double price = getDoubleInput();
        if (admin.updatePriceRoom(number, price)) {
            System.out.println("Стоимость номера " + number + " изменена на: " + price);
        } else {
            System.out.println("Номер не найден");
        }
    }

    private void updatePriceService() {
        System.out.print("Введите название услуги: ");
        String name = scanner.nextLine();
        System.out.print("Введите новую цену: ");
        double price = getDoubleInput();
        if (admin.updatePriceService(name, price)) {
            System.out.println("Стоимость услуги: " + name + " изменена на: " + price);
        } else {
            System.out.println("Услуга не найдена");
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
        System.out.print("Введите дату (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.println("Номера свободные к " + date + ":");
        for (Room room : admin.getRoomAvailableByDate(date)) {
            System.out.println("Номер: " + room.getNumber());
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

    private void printCountGuests() {
        long count = admin.getCountAvailableRooms();
        System.out.println("Всего свободных номеров: " + count);
        count = admin.getCountGuests();
        System.out.println("Всего жильцов: " + count);
    }

    private void printGuestServicesByPrice() {
        System.out.print("Введите имя гостя: ");
        String name = scanner.nextLine();
        List<ServiceRecord> records = admin.getGuestServicesSortedByPrice(name);
        if (records.isEmpty()) {
            System.out.println("У гостя " + name + " нет заказанных услуг");
            return;
        }
        System.out.println("Список услуг постояльца " + name + ", отсортированный по цене:");
        for (ServiceRecord record : records) {
            Service service = admin.getAllServices().stream()
                    .filter(s -> s.getName().equals(record.getName()))
                    .findFirst()
                    .orElse(null);
            if (service != null) {
                System.out.println("Услуга " + record.getName() + " за " + service.getPrice() + " от " + record.getDate());
            }
        }
    }

    private void printGuestServicesByDate() {
        System.out.print("Введите имя гостя: ");
        String name = scanner.nextLine();
        List<ServiceRecord> records = admin.getGuestServicesSortedByDate(name);
        if (records.isEmpty()) {
            System.out.println("У гостя " + name + " нет заказанных услуг");
            return;
        }
        System.out.println("Список услуг постояльца " + name + ", отсортированный по дате:");
        for (ServiceRecord record : records) {
            Service service = admin.getAllServices().stream()
                    .filter(s -> s.getName().equals(record.getName()))
                    .findFirst()
                    .orElse(null);
            if (service != null) {
                System.out.println("Услуга " + record.getName() + " за " + service.getPrice() + " от " + record.getDate());
            }
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
        System.out.println("\nЦены услуг:");
        for (Service service : admin.getAllServicesSortedByPrice()) {
            System.out.println("Услуга " + service.getName() + " стоит " + service.getPrice());
        }
    }

    private void printRoomDetails() {
        System.out.print("Введите номер комнаты: ");
        int number = getIntInput();
        Room room = admin.getRoomDetails(number);
        if (room != null) {
            System.out.println(room);
        } else {
            System.out.println("Номер не найден");
        }
    }

    // вспомогательные методы ввода, тк после nextInt может остаться \n в буфере
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