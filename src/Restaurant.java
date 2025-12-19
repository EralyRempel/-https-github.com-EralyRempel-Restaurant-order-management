public class Restaurant {
    private String name;
    private String address;

    public Restaurant(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return "Ресторан: " + name + " (Адрес: " + address + ")";
    }
}