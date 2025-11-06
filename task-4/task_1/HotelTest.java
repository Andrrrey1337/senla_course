package task_1;

import java.time.LocalDate;

public class HotelTest {
    public static void main(String[] args) {
        Admin admin = new Admin();
        LocalDate today = LocalDate.now();

        admin.addRoom(1,150, 2, 3);
        admin.addRoom(2,300,4, 5);
        admin.addRoom(3,250, 4,4);
        admin.addRoom(4,100,2,1);
        admin.addRoom(5,200,3,2);
        System.out.println();

        admin.addService("Обед", 30);
        admin.addService("Завтрак", 20);
        admin.addService("Ужин", 35);
        System.out.println();

        admin.checkIn(1, "Иван", today, today.plusDays(3));
        admin.checkIn(3, "Максим", today.minusDays(5), today.minusDays(2));
        admin.checkOut(3);
        admin.checkIn(3, "Константин", today,today.plusDays(3));
        admin.checkOut(3);
        admin.checkIn(3,"Андрей", today.plusDays(3), today.plusDays(6));
        System.out.println();

        admin.orderService("Иван", "Ужин", today);
        admin.orderService("Иван", "Завтрак", today.plusDays(2));
        admin.orderService("Иван", "Спа", today.plusDays(3));
        admin.orderService("Максим", "Обед", today.minusDays(2));
        System.out.println();

        admin.setRoomStatus(2, RoomStatus.REPAIR);
        System.out.println();

        admin.updatePriceRoom(1,140);
        admin.updatePriceService("Обед", 33);
        admin.updatePriceRoom(2,300);
        admin.updatePriceService("Ужин", 35);
        System.out.println();


        admin.printSortedRoomsByPrice();
        admin.printSortedRoomsByCapacity();
        admin.printSortedRoomsByStars();
        admin.printAvailableSortedRoomsByStars();
        admin.printAvailableSortedRoomsByCapacity();
        admin.printAvailableSortedRoomsByPrice();
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
