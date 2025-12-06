package model;

import java.io.Serializable;
import java.time.LocalDate;

public class ServiceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private final long guestId;
    private final long serviceId;
    public final LocalDate date;

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
