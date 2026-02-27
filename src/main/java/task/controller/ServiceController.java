package task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task.dto.ServiceDto;
import task.model.Service;
import task.service.managers.ServiceManager;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceManager serviceManager;

    private ServiceController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    //get запросы
    @GetMapping
    public List<ServiceDto> getServices(@RequestParam(required = false, defaultValue = "name") String sortBy) {
        List<Service> services;
        sortBy = sortBy.toLowerCase();

        services = sortBy.equals("price") ? serviceManager.getAllServicesSortedByPrice() : serviceManager.getAllServices();

        return services.stream()
                .map(this::convertToDto)
                .toList();
    }

    //post запросы
    @PostMapping
    public void createService(@RequestBody ServiceDto serviceDto) {
        serviceManager.addService(serviceDto.getName(), serviceDto.getPrice());
    }

    //put запросы
    @PutMapping("/{name}/price")
    public void updateService(@PathVariable String name, @RequestParam Double price) {
        serviceManager.updatePriceService(name, price);
    }

    private ServiceDto convertToDto(Service service) {
        ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        return dto;
    }
}
