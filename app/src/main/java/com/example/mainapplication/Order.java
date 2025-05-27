package com.example.mainapplication;

public class Order {
    private String orderId;
    private String restaurantName;
    private String details;
    private int quantity;

    private String status;
    private boolean isPaid;
    private String time;
    private boolean isRated;

    public Order(String orderId, String restaurantName, String details, int quantity, String status, boolean isPaid, String time) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.details = details;
        this.quantity = quantity;
        this.status = status;
        this.isPaid = isPaid;
        this.time = time;
        this.isRated = false;
    }

    public String getOrderId() {
        return orderId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDetails() {
        return details;
    }
    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRated() {
        return isRated;
    }

    public String getTime() {
        return time;
    }
    public boolean isPaid() {
        return isPaid;
    }

    public void setRated(boolean rated) {
        this.isRated = rated;
    }
    public String getOrderDetails() {
        return quantity + "x " + details + " from " + restaurantName;
    }
}
