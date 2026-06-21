package model;

public class Beverage extends MenuItem {
    private String beverageDetails; // e.g., "Medium / Dingin", "Large / Hangat"

    public Beverage(int id, String name, double price, String beverageDetails) {
        super(id, name, price, "BEVERAGE");
        this.beverageDetails = beverageDetails;
    }

    public String getBeverageDetails() {
        return beverageDetails;
    }

    public void setBeverageDetails(String beverageDetails) {
        this.beverageDetails = beverageDetails;
    }

    @Override
    public String getDetailInfo() {
        return "Suhu & Ukuran: " + beverageDetails;
    }
}
