package com.example.mainapplication;

public class Order {
    private String orderId;
    private String restaurantName;
    private String status;
    private boolean rated;

    public Order(String orderId, String restaurantName, String status, boolean rated) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.rated = rated;
    }

    public String getOrderId() {
        return orderId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public String getStatus() {
        return status;
    }
    public boolean isRated() {
        return rated;
    }
    public void setRated(boolean rated) {
        this.rated = rated;
    }
}