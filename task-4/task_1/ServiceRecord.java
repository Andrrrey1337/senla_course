package task_1;

import java.time.LocalDate;

public class ServiceRecord {
    public final String name;
    public final LocalDate date;

    public ServiceRecord(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }
}
