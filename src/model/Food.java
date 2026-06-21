package model;

public class Food extends MenuItem {
    private String spiciness; // e.g., "Tidak Pedas", "Pedas Sedang", "Sangat Pedas"

    public Food(int id, String name, double price, String spiciness) {
        super(id, name, price, "FOOD");
        this.spiciness = spiciness;
    }

    public String getSpiciness() {
        return spiciness;
    }

    public void setSpiciness(String spiciness) {
        this.spiciness = spiciness;
    }

    @Override
    public String getDetailInfo() {
        return "Tingkat Kepedasan: " + spiciness;
    }
}
