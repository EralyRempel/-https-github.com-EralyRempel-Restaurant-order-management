public class Restaurant {
    private String name;
    private String address;

    public Restaurant(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Restaurant: " + name + " | Address: " + address;
    }
}