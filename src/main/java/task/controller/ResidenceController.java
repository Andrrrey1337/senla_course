package task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.dto.ResidenceDto;
import task.model.Residence;
import task.model.Room;
import task.service.managers.ResidenceManager;
import task.service.managers.RoomManager;

import java.util.List;

@RestController
@RequestMapping("/api/residences")
public class ResidenceController {
    private ResidenceManager residenceManager;
    private RoomManager roomManager;

    private ResidenceController(ResidenceManager residenceManager,  RoomManager roomManager) {
        this.residenceManager = residenceManager;
        this.roomManager = roomManager;
    }

    @GetMapping("/room/{roomNumber}")
    public List<ResidenceDto> getRoomHistory(@PathVariable int roomNumber) {
        Room room = roomManager.getRoomDetails(roomNumber);
        return residenceManager.getLastByRoom(room.getId()).stream()
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
