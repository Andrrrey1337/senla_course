package task_1.model;

import task_1.IdGenerator.IdGenerator;

import java.time.LocalDate;

public class ServiceRecord {
    private final long id;
    private final long guestId;
    private final long serviceId;
    public final LocalDate date;

    public ServiceRecord(Long serviceId, Long guestId, LocalDate date) {
        this.id = IdGenerator.next();
        this.guestId = guestId;
        this.serviceId = serviceId;
        this.date = date;
    }

    public ServiceRecord(long id,  Long guestId, Long serviceId, LocalDate date) {
        this.id = id;
        this.guestId = guestId;
        this.serviceId = serviceId;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public LocalDate getDate() {
        return date;
    }
}
