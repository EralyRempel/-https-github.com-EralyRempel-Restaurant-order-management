public class Order {
    private int orderId;
    private String status;

    public Order(int orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Заказ №" + orderId + " [Статус: " + status + "]";
    }
}