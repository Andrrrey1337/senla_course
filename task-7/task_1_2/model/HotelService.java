package model;

import exceptions.HotelException;
import util.IdGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class HotelService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static HotelService instance;
    private final IdGenerator idGeneratorState = new IdGenerator();
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;
    private final GuestManager guestManager;
    private final ServiceRecordManager serviceRecordManager;
    private final BookingManager bookingManager;
    private final DataManager dataManager;

    private HotelService() {
        this.roomManager = new RoomManager(idGeneratorState);
        this.serviceManager = new ServiceManager(idGeneratorState);
        this.guestManager = new GuestManager(idGeneratorState);
        this.serviceRecordManager = new ServiceRecordManager(idGeneratorState, guestManager, serviceManager);
        this.bookingManager = new BookingManager(roomManager, guestManager);
        this.dataManager = new DataManager(roomManager, serviceManager, guestManager, serviceRecordManager, idGeneratorState);
    }

    public static void setInstanceForLoading(HotelService loadedHotelService) {
        if (instance == null) {
            instance = loadedHotelService;
        }
    }

    public static HotelService getInstance() {
        if (instance == null) {
            instance = new HotelService();
        }
        return instance;
    }

    // методы RoomManager
    public void addRoom(int number, double price, int capacity, int stars) throws HotelException {
        roomManager.addRoom(number, price, capacity, stars);
    }

    public void updatePriceRoom(int number, double newPrice) throws HotelException {
        roomManager.updatePriceRoom(number, newPrice);
    }

    public void setRoomStatus(int number, RoomStatus status) throws HotelException {
        roomManager.setRoomStatus(number, status);
    }

    public List<Room> getAllRooms() {
        return roomManager.getAllRooms();
    }

    public List<Room> getSortedRooms(Comparator<Room> comparator) {
        return roomManager.getSortedRooms(comparator);
    }

    public List<Room> getAvailableRooms(Comparator<Room> comparator) {
        return roomManager.getAvailableRooms(comparator);
    }

    public List<Room> getRoomAvailableByDate(LocalDate date) {
        return roomManager.getRoomAvailableByDate(date);
    }

    public List<Room> getGuests(Comparator<Room> comparator) {
        return roomManager.getGuests(comparator);
    }

    public long getCountAvailableRooms() {
        return roomManager.getCountAvailableRooms();
    }

    public long getCountGuests() {
        return roomManager.getCountGuests();
    }

    public double getPaymentForRoom(int number) {
        return roomManager.getPaymentForRoom(number);
    }

    public List<Residence> getThreeLastGuests(int number) {
        return roomManager.getThreeLastGuests(number);
    }

    public List<Room> getAllRoomsSortedByPrice() {
        return roomManager.getAllRoomsSortedByPrice();
    }

    public Room getRoomDetails(int number) throws HotelException {
        return roomManager.getRoomDetails(number);
    }

    // методы ServiceManager
    public void addService(String name, double price) throws HotelException {
        serviceManager.addService(name, price);
    }

    public void updatePriceService(String name, double newPrice) throws HotelException {
        serviceManager.updatePriceService(name, newPrice);
    }

    public List<Service> getAllServices() {
        return serviceManager.getAllServices();
    }

    public List<Service> getAllServicesSortedByPrice() {
        return serviceManager.getAllServicesSortedByPrice();
    }

    // методы BookingManager
    public void checkIn(int number, String guestName, LocalDate checkInDate, LocalDate checkOutDate) throws HotelException {
        bookingManager.checkIn(number, guestName, checkInDate, checkOutDate);
    }

    public void checkOut(int number) throws HotelException {
        bookingManager.checkOut(number);
    }

    // методы ServiceRecordManager
    public void orderService(String guestName, String serviceName, LocalDate date) throws HotelException {
        serviceRecordManager.orderService(guestName, serviceName, date);
    }

    public List<ServiceRecord> getGuestServicesSortedByPrice(String guestName) throws HotelException {
        return serviceRecordManager.getGuestServicesSortedByPrice(guestName);
    }

    public List<ServiceRecord> getGuestServicesSortedByDate(String guestName) throws HotelException {
        return serviceRecordManager.getGuestServicesSortedByDate(guestName);
    }

    // методы DataManager
    public void exportGuests(String filePath) throws HotelException {
        dataManager.exportGuests(filePath);
    }

    public void importGuests(String filePath) throws HotelException {
        dataManager.importGuests(filePath);
    }

    public void exportServices(String path) throws HotelException {
        dataManager.exportServices(path);
    }

    public void importServices(String path) throws HotelException {
        dataManager.importServices(path);
    }

    public void exportRooms(String path) throws HotelException {
        dataManager.exportRooms(path);
    }

    public void importRooms(String path) throws HotelException {
        dataManager.importRooms(path);
    }

    public void exportServiceRecords(String path) throws HotelException {
        dataManager.exportServiceRecords(path);
    }

    public void importServiceRecords(String path) throws HotelException {
        dataManager.importServiceRecords(path);
    }
}