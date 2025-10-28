package task_4;

import java.util.HashMap;
import java.util.Map;

public class Admin {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<String, Service> services = new HashMap<>();

    public void checkIn(int number, String guestName) {
        Room room = rooms.get(number);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Номер не доступен");
            return;
        }
        room.setGuestName(guestName);
        room.setStatus(RoomStatus.OCCUPIED);
        System.out.println("Гость: " + guestName + " заселен в номер: " + number);
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
        System.out.println("Гость: " + room.getGuestName() + " выселен из номера: " + number);
        room.setGuestName("None");
        room.setStatus(RoomStatus.AVAILABLE);
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

    public void addRoom(int number, double price) {
        if (rooms.containsKey(number)) {
            System.out.println("Комната с таким номером уже существует: " + number);
            return;
        }
        rooms.put(number, new Room(number, price));
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
}
