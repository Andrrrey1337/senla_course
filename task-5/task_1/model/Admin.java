package task_1.model;

import java.time.LocalDate;
import java.util.*;


public class Admin {
    private static Admin instance;
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<String, Service> services = new HashMap<>();
    private final Map<Guest,List<ServiceRecord>> serviceRecords = new HashMap<>();
    private final Map<String, Guest> guests = new HashMap<>();

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    private Guest createOrFindGuest(String name) {
        if (guests.containsKey(name)){
            return guests.get(name);
        }
        Guest guest = new Guest(name);
        guests.put(name,guest);
        return guest;
    }

    public boolean orderService(String guestName, String serviceName, LocalDate date) {
        if (!services.containsKey(serviceName)) {
            return false;
        }
        Guest guest = createOrFindGuest(guestName);
        List<ServiceRecord> records = serviceRecords.get(guest);
        if (records == null) {
            records = new ArrayList<>();
            serviceRecords.put(guest, records);
        }
        records.add(new ServiceRecord(serviceName, date));
        return true;
    }

    public boolean checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = rooms.get(number);
        if (room == null || room.getStatus() != RoomStatus.AVAILABLE) {
            return false;
        }
        Guest guest = createOrFindGuest(guestName);
        room.setGuest(guest);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setCheckInDate(checkInDate);
        room.setCheckOutDate(checkOutDate);
        room.addResidence(guest,checkInDate,checkOutDate);
        return true;
    }

    public boolean checkOut(int number) {
        Room room = rooms.get(number);
        if (room == null || room.getStatus() != RoomStatus.OCCUPIED) {
            return false;
        }
        room.setGuest(null);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCheckOutDate(null);
        return true;
    }

    public boolean setRoomStatus(int number, RoomStatus status) {
        Room room = rooms.get(number);
        if (room == null) {
            return false;
        }
        room.setStatus(status);
        return true;
    }

    public boolean updatePriceRoom(int number, double newPrice) {
        Room room = rooms.get(number);
        if (room == null) {
            return false;
        }
        room.setPrice(newPrice);
        return true;
    }

    public boolean updatePriceService(String name, double newPrice) {
        Service service = services.get(name);
        if (service == null) {
            return false;
        }
        service.setPrice(newPrice);
        return true;
    }

    public boolean addRoom(int number, double price, int capacity, int stars) {
        if (rooms.containsKey(number)) {
            return false;
        }
        rooms.put(number, new Room(number, price, capacity, stars));
        return true;
    }

    public boolean addService(String name, double price) {
        if (services.containsKey(name)) {
            return false;
        }
        services.put(name, new Service(name, price));
        return true;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Service> getAllServices() {
        return new ArrayList<>(services.values());
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

    public List<ServiceRecord> getGuestServicesSortedByPrice(String guestName) {
        Guest guest = guests.get(guestName);
        if (guest == null || !serviceRecords.containsKey(guest)) {
            return Collections.emptyList();
        }
        return serviceRecords.get(guest).stream()
                .sorted(Comparator.comparingDouble(record -> services.get(record.getName()).getPrice()))
                .toList();
    }

    public List<ServiceRecord> getGuestServicesSortedByDate(String guestName) {
        Guest guest = guests.get(guestName);
        if (guest == null || !serviceRecords.containsKey(guest)) {
            return Collections.emptyList();
        }
        return serviceRecords.get(guest).stream()
                .sorted(Comparator.comparing(ServiceRecord::getDate))
                .toList();
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .toList();
    }

    public List<Service> getAllServicesSortedByPrice() {
        return services.values().stream()
                .sorted(Comparator.comparing(Service::getPrice))
                .toList();
    }

    public Room getRoomDetails(int number) {
        return rooms.get(number);
    }
}
