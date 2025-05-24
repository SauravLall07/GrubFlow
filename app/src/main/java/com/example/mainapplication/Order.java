package com.example.mainapplication;

public class Order {
    private String orderId;
    private String details;
    private String status;
    private boolean isPaid;
    private boolean rated;

    public Order(String orderId, String restaurantName, String status, boolean rated) {
        this.orderId = orderId;
        this.details = restaurantName;
        this.status = status;
        this.isPaid = isPaid;
        this.rated = false;
    }

    public String getOrderId() {
        return orderId;
    }
    public String getDetails() {
        return details;
    }
    public String getStatus() {
        return status;
    }
    public boolean isRated() {
        return rated;
    }
    public boolean isPaid() {
        return isPaid; }
    public void setRated(boolean rated) {
        this.rated = rated;
    }
}