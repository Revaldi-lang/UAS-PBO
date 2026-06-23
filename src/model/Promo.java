package model;

public class Promo {
    private String code;
    private double discountPercent;
    private double maxDiscount;
    private double minPurchase;

    public Promo(String code, double discountPercent, double maxDiscount, double minPurchase) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.maxDiscount = maxDiscount;
        this.minPurchase = minPurchase;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public double getMinPurchase() {
        return minPurchase;
    }
}
