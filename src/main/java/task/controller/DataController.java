package task.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.service.DataManager;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final DataManager dataManager;

    public DataController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    //экспорт
    @PostMapping("/export/guests")
    public void exportGuests() {
        dataManager.exportGuests();
    }

    @PostMapping("/export/rooms")
    public void exportRooms() {
        dataManager.exportRooms();
    }

    @PostMapping("/export/services")
    public void exportServices() {
        dataManager.exportServices();
    }

    @PostMapping("/export/service-records")
    public void exportServiceRecords() {
        dataManager.exportServiceRecords();
    }

    //импорт
    @PostMapping("/import/guests")
    public void importGuests() {
        dataManager.importGuests();
    }

    @PostMapping("/import/rooms")
    public void importRooms() {
        dataManager.importRooms();
    }

    @PostMapping("/import/services")
    public void importServices() {
        dataManager.importServices();
    }

    @PostMapping("/import/service-records")
    public void importServiceRecords() {
        dataManager.importServiceRecords();
    }
}
