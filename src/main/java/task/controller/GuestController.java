package task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task.dto.GuestDto;
import task.model.Guest;
import task.service.managers.GuestManager;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {
    private final GuestManager guestManager;

    public GuestController(GuestManager guestManager) {
        this.guestManager = guestManager;
    }

    //get запросы
    @GetMapping
    public List<GuestDto> getGuests() {
        return guestManager.getAllGuests().stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public GuestDto getGuestById(@PathVariable long id) {
        return convertToDto(guestManager.getGuestById(id));
    }

    @GetMapping("/search")
    public GuestDto getGuestByName(@RequestParam String name) {
        return convertToDto(guestManager.getGuestByName(name));
    }

    //post запросы - создание
    @PostMapping
    public GuestDto createGuest(@RequestBody GuestDto guestDto) {
        return convertToDto(guestManager.createOrFindGuest(guestDto.getName()));
    }

    //put запросы - обновление
    @PutMapping("/{id}")
    public void updateGuest(@PathVariable long id, @RequestBody GuestDto guestDto) {
        guestManager.updateOrCreateGuest(id, guestDto.getName());
    }



    private GuestDto convertToDto(Guest guest) {
        GuestDto dto = new GuestDto();
        dto.setId(guest.getId());
        dto.setName(guest.getName());
        return dto;
    }
}
