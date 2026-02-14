package task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "servicerecord")
public class ServiceRecord {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "date")
    private LocalDate date;

    public ServiceRecord() { }

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
