package task.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task.dto.RoomDto;
import task.model.Room;
import task.model.RoomStatus;
import task.service.managers.RoomManager;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController // @Controller + @ResponseBody(значение возврата метода должно быть привязано к телу HTTP ответа)
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    // Get запросы
    @GetMapping("/{number}")
    public RoomDto getRoomByNumber(@PathVariable int number) {
        return convertToDto(roomManager.getRoomDetails(number));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms(@RequestParam(required = false, defaultValue = "number") String sortBy) {
        sortBy = sortBy.toLowerCase();
        Comparator<Room> comparator = sortBy.equals("price")
                ? Comparator.comparing(Room::getPrice)
                : Comparator.comparing(Room::getNumber);

        return roomManager.getSortedRooms(comparator).stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/available")
    @Transactional(readOnly = true)
    public List<RoomDto> getAvailableRooms(@RequestParam(required = false, defaultValue = "number") String sortBy) {
        Comparator<Room> comparator;

        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(Room::getPrice);
                break;
            case "capacity":
                comparator = Comparator.comparing(Room::getCapacity);
                break;
            case "stars":
                comparator = Comparator.comparing(Room::getStars);
                break;
            case "number":
            default:
                comparator = Comparator.comparing(Room::getNumber);
                break;
        }

        return roomManager.getAvailableRooms(comparator).stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/available/count")
    public long getCountAvailableRooms() {
        return roomManager.getCountAvailableRooms();
    }

    @GetMapping("/available/date")
    @Transactional(readOnly = true)
    public List<RoomDto> getRoomAvailableByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return roomManager.getRoomAvailableByDate(date).stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/occupied")
    @Transactional(readOnly = true)
    public List<RoomDto> getOccupiedRooms(@RequestParam(required = false, defaultValue = "number") String sortBy) {
        Comparator<Room> comparator;

        switch (sortBy.toLowerCase()) {
            case "date":
                // сортировка по дате выселения
                comparator = Comparator.comparing(Room::getCheckOutDate, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "price":
                comparator = Comparator.comparing(Room::getPrice);
                break;
            case "number":
            default:
                comparator = Comparator.comparing(Room::getNumber);
                break;
        }

        return roomManager.getGuests(comparator).stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/occupied/count")
    public long getCountGuests() {
        return roomManager.getCountGuests();
    }

    @GetMapping("/{number}/payment")
    public double getPaymentForRoom(@PathVariable int number) {
        return roomManager.getPaymentForRoom(number);
    }


    // post запросы(создание)
    @PostMapping
    public void addRoom(@RequestBody RoomDto roomDto) {
        roomManager.addRoom(roomDto.getNumber(), roomDto.getPrice(), roomDto.getCapacity(), roomDto.getStars());
    }


    //put запросы - обновление
    @PutMapping("/{number}/price")
    public void updateRoomPrice(@PathVariable int number, @RequestBody Double newPrice) {
        roomManager.updatePriceRoom(number, newPrice);
    }

    @PutMapping("/{number}/status")
    public void updateRoomStatus(@PathVariable int number, @RequestBody RoomStatus roomStatus) {
        roomManager.setRoomStatus(number, roomStatus);
    }


    // entity в dto
    private RoomDto convertToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setNumber(room.getNumber());
        dto.setPrice(room.getPrice());
        dto.setCapacity(room.getCapacity());
        dto.setStars(room.getStars());
        dto.setStatus(room.getStatus().name());

        if (room.getGuest() != null) {
            dto.setGuestId(room.getGuest().getId());
        }

        dto.setCheckInDate(room.getCheckInDate());
        dto.setCheckOutDate(room.getCheckOutDate());

        return dto;
    }
}
