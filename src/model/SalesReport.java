package model;

import java.util.List;

public class SalesReport {
    private double totalRevenue;
    private double totalDiscounts;
    private int completedOrdersCount;
    private List<BestSeller> topSellingItems;

    public SalesReport(double totalRevenue, double totalDiscounts, int completedOrdersCount, List<BestSeller> topSellingItems) {
        this.totalRevenue = totalRevenue;
        this.totalDiscounts = totalDiscounts;
        this.completedOrdersCount = completedOrdersCount;
        this.topSellingItems = topSellingItems;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getTotalDiscounts() {
        return totalDiscounts;
    }

    public int getCompletedOrdersCount() {
        return completedOrdersCount;
    }

    public List<BestSeller> getTopSellingItems() {
        return topSellingItems;
    }

    public static class BestSeller {
        private String name;
        private int quantity;

        public BestSeller(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
