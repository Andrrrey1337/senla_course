package task_2_3_4.model;

import java.time.LocalDate;

public class ServiceRecord {
    private final long id;
    private final long guestId;
    private final long serviceId;
    private final LocalDate date;

    public ServiceRecord(long id, Long guestId, Long serviceId, LocalDate date) {
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
