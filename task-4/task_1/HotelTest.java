package task_1;

import java.time.LocalDate;

public class HotelTest {
    public static void main(String[] args) {
        Admin admin = new Admin();
        LocalDate today = LocalDate.now();

        admin.addRoom(1,150, 2, 3);
        admin.addRoom(2,300,4, 5);
        admin.addRoom(3,250, 4,4);
        System.out.println();

        admin.addService("Обед", 30);
        admin.addService("Завтрак", 20);
        System.out.println();

        admin.checkIn(1, "Иван", today, today.plusDays(3));
        admin.checkIn(3, "Максим", today, today.plusDays(5));
        System.out.println();

        admin.orderService("Иван", "Завтрак", today);
        admin.orderService("Иван", "Спа", today.plusDays(1));
        admin.orderService("Максим", "Обед", today.plusDays(2));
        System.out.println();

        admin.setRoomStatus(2, RoomStatus.REPAIR);
        System.out.println();

        admin.checkOut(3);
        System.out.println();

        admin.updatePriceRoom(1,140);
        admin.updatePriceService("Обед", 33);
        System.out.println();


        admin.printSortedRoomsByPrice();
        admin.printAvailableSortedRoomsByStars();
        admin.printGuestsByAlphabet();
        admin.printGuestsByCheckOutDate();
        System.out.println();
        admin.printCountAvailableRooms();
        admin.printCountGuests();
        System.out.println();
        admin.printRoomAvailableByDate(today.plusDays(4));
        System.out.println();
        admin.printPaymentForRoom(1);
        System.out.println();
        admin.printThreeLastGuests(3);
        System.out.println();
        admin.printGuestServicesByPrice("Иван");
        admin.printGuestServicesByDate("Иван");
        System.out.println();
        admin.printAllPrices();
        System.out.println();
        admin.printRoomDetails(1);
        admin.printRooms();
        admin.printServices();
    }
}
