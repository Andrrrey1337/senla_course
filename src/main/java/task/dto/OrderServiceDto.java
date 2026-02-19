package task.dto;

import java.time.LocalDate;

//заказ услуги
public class OrderServiceDto {
    private String guestName;
    private String serviceName;
    private LocalDate date;

    public OrderServiceDto() {
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}