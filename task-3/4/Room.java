public class Room {
    private int number;
    private double price;
    private String guestName;
    private RoomStatus status;

    public Room(int number, double price) {
        this.number = number;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
        this.guestName = "None";
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Номер: " + number + ", " + "Стоимость номера: " + price + ", " + "статус номера: " + number + ", " +
                "имя гостя: " + guestName;
    }
}
