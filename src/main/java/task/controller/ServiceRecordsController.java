package task.controller;

import org.springframework.web.bind.annotation.*;
import task.dto.OrderServiceDto;
import task.dto.ServiceRecordDto;
import task.model.ServiceRecord;
import task.service.managers.ServiceRecordManager;

import java.util.List;

@RestController
@RequestMapping(("/api/service-records"))
public class ServiceRecordsController {
    private final ServiceRecordManager serviceRecordManager;

    public ServiceRecordsController(ServiceRecordManager serviceRecordManager) {
        this.serviceRecordManager = serviceRecordManager;
    }

    //get запросы
    @GetMapping
    public List<ServiceRecordDto> getAllRecords() {
        return serviceRecordManager.getAllRecords().stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("/{guestName}")
    public List<ServiceRecordDto> getRecordsByGuestName(
            @PathVariable String guestName,
            @RequestParam(required = false, defaultValue = "date") String sortBy) {

        List<ServiceRecord> records;
        sortBy = sortBy.toLowerCase();

        records = sortBy.equals("price")
                ? serviceRecordManager.getGuestServicesSortedByPrice(guestName)
                : serviceRecordManager.getGuestServicesSortedByDate(guestName);

        return records.stream()
                .map(this::convertToDto)
                .toList();
    }

    //post запросы
    @PostMapping("/order")
    public void orderService(@RequestBody OrderServiceDto request) {
        serviceRecordManager.orderService(request.getGuestName(), request.getServiceName(), request.getDate());
    }

    private ServiceRecordDto convertToDto(ServiceRecord record) {
        ServiceRecordDto dto = new ServiceRecordDto();
        dto.setId(record.getId());
        dto.setGuestId(record.getGuestId());
        dto.setServiceId(record.getServiceId());
        dto.setDate(record.getDate());
        return dto;
    }
}
