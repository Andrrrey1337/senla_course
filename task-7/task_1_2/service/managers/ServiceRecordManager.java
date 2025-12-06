package service.managers;

import exceptions.HotelException;
import model.Guest;
import model.Service;
import model.ServiceRecord;
import util.IdGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class ServiceRecordManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Long, List<ServiceRecord>> serviceRecordsByGuestId = new HashMap<>();
    private final Map<Long, Service> servicesById = new HashMap<>();
    private final GuestManager guestManager;
    private final ServiceManager serviceManager;
    private final IdGenerator idGeneratorState;

    public ServiceRecordManager(IdGenerator idGenerator, GuestManager guestManager, ServiceManager serviceManager) {
        this.idGeneratorState = idGenerator;
        this.guestManager = guestManager;
        this.serviceManager = serviceManager;
        for (Service service : serviceManager.getAllServices()) {
            servicesById.put(service.getId(), service);
        }
    }

    public void orderService(String guestName, String serviceName, LocalDate date) throws HotelException {
        if (!serviceManager.getAllServices().stream().anyMatch(s -> s.getName().equals(serviceName))) {
            throw new HotelException("Услуга с названием '" + serviceName + "' не найдена");
        }
        Guest guest = guestManager.createOrFindGuest(guestName);
        List<ServiceRecord> records = serviceRecordsByGuestId.computeIfAbsent(guest.getId(), k -> new ArrayList<>());
        records.add(new ServiceRecord(idGeneratorState.next(), guest.getId(), serviceManager.getAllServices().stream()
                .filter(s -> s.getName().equals(serviceName)).findFirst().get().getId(), date));
    }

    public List<ServiceRecord> getGuestServicesSortedByPrice(String guestName) throws HotelException {
        Guest guest = guestManager.getGuestByName(guestName);
        if (!serviceRecordsByGuestId.containsKey(guest.getId())) {
            return Collections.emptyList();
        }
        return serviceRecordsByGuestId.get(guest.getId()).stream()
                .sorted(Comparator.comparingDouble(record -> servicesById.get(record.getServiceId()).getPrice()))
                .toList();
    }

    public List<ServiceRecord> getGuestServicesSortedByDate(String guestName) throws HotelException {
        Guest guest = guestManager.getGuestByName(guestName);
        if (!serviceRecordsByGuestId.containsKey(guest.getId())) {
            return Collections.emptyList();
        }
        return serviceRecordsByGuestId.get(guest.getId()).stream()
                .sorted(Comparator.comparing(ServiceRecord::getDate))
                .toList();
    }

    public List<ServiceRecord> getAllRecords() {
        List<ServiceRecord> allRecords = new ArrayList<>();
        for (List<ServiceRecord> records : serviceRecordsByGuestId.values()) {
            allRecords.addAll(records);
        }
        return allRecords;
    }

    //  методы для DataManager
    public void updateOrCreateRecord(long id, long guestId, long serviceId, LocalDate date) {
        List<ServiceRecord> list = serviceRecordsByGuestId.computeIfAbsent(guestId, l -> new ArrayList<>());
        ServiceRecord exists = list.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
        if (exists != null) {
            list.remove(exists);
        }
        ServiceRecord rec = new ServiceRecord(id, guestId, serviceId, date);
        serviceRecordsByGuestId.get(guestId).add(rec);
    }
}