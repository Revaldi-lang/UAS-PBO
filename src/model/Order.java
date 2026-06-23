package model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private String orderDate;
    private double totalPrice;
    private String paymentMethod; // "CASH" or "E-WALLET"
    private String status; // "PENDING", "COMPLETED", "CANCELLED"
    private List<OrderItem> orderItems;
    private double discountAmount;
    private String promoCode;

    public Order(int id, String customerName, String orderDate, double totalPrice, String paymentMethod, String status) {
        this(id, customerName, orderDate, totalPrice, paymentMethod, status, 0.0, "");
    }

    public Order(int id, String customerName, String orderDate, double totalPrice, String paymentMethod, String status, double discountAmount, String promoCode) {
        this.id = id;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.discountAmount = discountAmount;
        this.promoCode = promoCode;
        this.orderItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
