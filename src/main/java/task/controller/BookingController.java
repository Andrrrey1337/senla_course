package task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task.dto.CheckInRequest;
import task.service.managers.BookingManager;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingManager bookingManager;

    public BookingController(BookingManager bookingManager) {
        this.bookingManager = bookingManager;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkIn(@RequestBody CheckInRequest request) {
        bookingManager.checkIn(
                request.getRoomNumber(),
                request.getGuestName(),
                request.getCheckInDate(),
                request.getCheckOutDate());
    }

    @PostMapping("/check-out/{roomNumber}")
    public void checkOut(@PathVariable int roomNumber) {
        bookingManager.checkOut(roomNumber);
    }
}
