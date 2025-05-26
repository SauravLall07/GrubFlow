package com.example.mainapplication;

public class Order {
    private String orderId;
    private String details;
    private String status;
    private boolean isPaid;
    private boolean isRated;

    public Order(String orderId, String details, String status, boolean isPaid, boolean isRated) {
        this.orderId = orderId;
        this.details = details;
        this.status = status;
        this.isPaid = isPaid;
        this.isRated = isRated;
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
        return isRated;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setRated(boolean rated) {
        this.isRated = rated;
    }
}
