package task_4;

public class HotelTest {
    public static void main(String[] args) {
        Admin admin = new Admin();

        admin.addRoom(1,150);
        admin.addRoom(2,300);
        admin.addRoom(3,250);

        admin.addService("Обед", 30);
        admin.addService("Завтрак", 20);

        admin.checkIn(1, "Иван");
        admin.checkIn(3, "Максим");

        admin.setRoomStatus(2, RoomStatus.REPAIR);

        admin.checkOut(3);

        admin.updatePriceRoom(1,140);
        admin.updatePriceService("Обед", 33);

        admin.printRooms();
        admin.printServices();
    }
}
