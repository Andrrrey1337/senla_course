package task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.dto.ResidenceDto;
import task.model.Residence;
import task.service.managers.ResidenceManager;

import java.util.List;

@RestController
@RequestMapping("/api/residences")
public class ResidenceController {
    private ResidenceManager residenceManager;

    private ResidenceController(ResidenceManager residenceManager) {
        this.residenceManager = residenceManager;
    }

    @GetMapping("/room/{roomId}")
    public List<ResidenceDto> getRoomHistory(@PathVariable long roomId) {
        return residenceManager.getLastByRoom(roomId).stream()
                .map(this::convertToDto)
                .toList();
    }

    private ResidenceDto convertToDto(Residence residence) {
        ResidenceDto dto = new ResidenceDto();
        dto.setId(residence.getId());
        dto.setGuestId(residence.getGuestId());
        dto.setRoomId(residence.getRoomId());
        dto.setCheckInDate(residence.getCheckInDate());
        dto.setCheckOutDate(residence.getCheckOutDate());
        return dto;
    }
}
