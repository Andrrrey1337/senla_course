package task_1;

import java.time.LocalDate;
import java.util.*;


public class Admin {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<String, Service> services = new HashMap<>();
    private final Map<Guest,List<ServiceRecord>> serviceRecords = new HashMap<>();
    private final Map<String, Guest> guests = new HashMap<>();

    private Guest createOrFindGuest(String name) {
        if (guests.containsKey(name)){
            return guests.get(name);
        }
        Guest guest = new Guest(name);
        guests.put(name,guest);
        return guest;
    }

    public void orderService(String guestName, String serviceName, LocalDate date) {
        if (!services.containsKey(serviceName)) {
            System.out.println("Такая услуга не найдена: " + serviceName);
            return;
        }
    }

    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Номер не доступен");
            return;
        }
        Guest guest = createOrFindGuest(guestName);
        room.setGuest(guest);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setCheckInDate(checkInDate);
        room.setCheckOutDate(checkOutDate);
        room.addResidence(guest,checkInDate,checkOutDate);
        System.out.println("Гость: " + guest + " заселен в номер: " + number);
    }

    public void checkOut(int number) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            System.out.println("Комната итак свободна");
            return;
        }
        System.out.println("Гость: " + room.getGuest() + " выселен из номера: " + number);
        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckOutDate(null);
        room.setCheckOutDate(null);
    }

    public void setRoomStatus(int number, RoomStatus status) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        room.setStatus(status);
        System.out.println("Статус номера: " + number + " изменен на: " + status);
    }

    public void updatePriceRoom(int number, double newPrice) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        room.setPrice(newPrice);
        System.out.println("Стоимость номера " + number + " изменена на: " + newPrice);
    }

    public void updatePriceService(String name, double newPrice) {
        Service service = services.get(name);
        if (service == null) {
            System.out.println("Услуга не найдена");
            return;
        }
        service.setPrice(newPrice);
        System.out.println("Стоимость услуги: " + name + " изменена на: " + newPrice);
    }

    public void addRoom(int number, double price, int capacity, int stars) {
        if (rooms.containsKey(number)) {
            System.out.println("Комната с таким номером уже существует: " + number);
            return;
        }
        rooms.put(number, new Room(number, price, capacity, stars));
        System.out.println("Комната с номером: " + number + " и стоимостью: " + price + " добавлена");
    }

    public void addService(String name, double price) {
        if (services.containsKey(name)) {
            System.out.println("Услуга с таким названием уже существует: " + name);
            return;
        }
        services.put(name, new Service(name, price));
        System.out.println("Добавлена услуга с названием: " + name + " и стоимостью: " + price);
    }

    public void printRooms() {
        System.out.println("Все номера в отеле: ");
        for (Room room : rooms.values()) {
            System.out.println(room);
        }
    }

    public void printServices() {
        System.out.println("Все предоставляемые услуги: ");
        for (Service service : services.values()) {
            System.out.println(service);
        }
    }
    private void printSortedRooms(Comparator<Room> comparator){
        List<Room> sorted = rooms.values().stream()
                .sorted(comparator)
                .toList();
        for (Room room : sorted) {
            System.out.println(room);
        }
    }
    public void printSortedRoomsByPrice() {
        System.out.println("Номера отсортированы по цене: ");
        printSortedRooms(Comparator.comparingDouble(Room::getPrice));
    }

    public void printSortedRoomsByCapacity() {
        System.out.println("Номера отсортированы вместительности: ");
        printSortedRooms(Comparator.comparingInt(Room::getCapacity));
    }

    public void printSortedRoomsByStars() {
        System.out.println("Номера отсортированы по звездам: ");
        printSortedRooms(Comparator.comparingInt(Room::getStars));
    }

    private void printAvailableRooms(Comparator<Room> comparator) {
        List<Room> available = rooms.values().stream()
                .filter(q -> q.getStatus() == RoomStatus.AVAILABLE)
                .sorted(comparator)
                .toList();
        for (Room room : available) {
            System.out.println(room);
        }
    }

    public void printAvailableSortedRoomsByPrice() {
        System.out.println("Свободные номера(отсортированы по цене): ");
        printAvailableRooms(Comparator.comparingDouble(Room::getPrice));
    }
    public void printAvailableSortedRoomsByCapacity() {
        System.out.println("Свободные номера(отсортированы по вместительности): ");
        printAvailableRooms(Comparator.comparingDouble(Room::getCapacity));
    }
    public void printAvailableSortedRoomsByStars() {
        System.out.println("Свободные номера(отсортированы по звездам): ");
        printAvailableRooms(Comparator.comparingDouble(Room::getStars));
    }

    private void printGuests(Comparator<Room> comparator) {
        List<Room> occupied = rooms.values().stream()
                .filter(q -> q.getStatus() == RoomStatus.OCCUPIED)
                .sorted(comparator)
                .toList();
        for (Room room : occupied) {
            System.out.println(room.getGuest().getName() + " выселится " + room.getCheckOutDate() + " из номера " + room.getNumber());
        }
    }

    public void printGuestsByAlphabet() {
        System.out.println("Список постояльцев(отсортирован по алфавиту): ");
        printGuests(Comparator.comparing(room -> room.getGuest().getName()));
    }

    public void printGuestsByCheckOutDate() {
        System.out.println("Список постояльцев(отсортирован по дате выселения): ");
        printGuests(Comparator.comparing(Room::getCheckOutDate));
    }

    public void printCountAvailableRooms() {
        long count = rooms.values().stream()
                .filter(q -> q.getStatus() == RoomStatus.AVAILABLE)
                .count();
        System.out.println("Всего свободных номеров: " + count);
    }

    public void printCountGuests() {
        long count = rooms.values().stream()
                .filter(q -> q.getStatus() == RoomStatus.OCCUPIED)
                .count();
        System.out.println("Всего жильцов: " + count);
    }

    public void printRoomAvailableByDate(LocalDate date) {
        List<Room> available = rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE ||
                        (room.getCheckOutDate() != null && date.isAfter(room.getCheckOutDate())))
                .toList();
        System.out.println("Номера свободные к " + date +':');
        for (Room room : available) {
            System.out.println("Номер: " + room.getNumber());
        }
    }

    public void printPaymentForRoom(int number) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        double payment = room.calculatePayment();
        System.out.println("Сумма к оплате за номер " + number + ": " + payment);
    }

    public void printThreeLastGuests(int number) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        List<Residence> history = room.getResidence();
        System.out.println("Последние 3 постояльца номера " + number + ": ");
        for (Residence residence : history) {
            System.out.println(residence.getGuest());
        }
    }

    private void printGuestServices(Comparator<ServiceRecord> comparator, String guestName) {
        Guest guest = guests.get(guestName);
        if (guest == null || !serviceRecords.containsKey(guest)) {
            System.out.println("У гостя " + guestName + " нет заказанных услуг");
            return;
        }
        List<ServiceRecord> records = serviceRecords.get(guest);
        records = records.stream()
                .sorted(comparator)
                .toList();
        for (ServiceRecord record : records) {
            System.out.println("Услуга " + record.getName() + " за " + services.get(record.getName()) + " от " + record.getDate());
        }
    }

    public void printGuestServicesByPrice(String guestName) {
        System.out.println("Список услуг постояльца " + guestName + ", отсортированный по алфавиту: ");
        printGuestServices(Comparator.comparingDouble(ServiceRecord->services.get(ServiceRecord.getName()).getPrice()),guestName);
    }

    public void printGuestServicesByDate(String guestName) {
        System.out.println("Список услуг постояльца " + guestName + ", отсортированный по дате выселения: ");
        printGuestServices(Comparator.comparing(ServiceRecord::getDate),guestName);
    }

    public void printAllPrices() {
        System.out.println("Цены на номера: ");
        List<Room> sorted = rooms.values().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
        for (Room room : sorted) {
            System.out.println("Номер " + room.getNumber() + " стоит " + room.getPrice());
        }
        System.out.println("\nЦены услуг: ");
        List<Service> sortedServices = services.values().stream()
                .sorted(Comparator.comparing(Service::getPrice))
                .toList();
        for (Service service : sortedServices) {
            System.out.println("Услуга " + service.getName() + " стоит " + service.getPrice());
        }
    }

    public void printRoomDetails(int number) {
        System.out.println(rooms.get(number));
    }
}
