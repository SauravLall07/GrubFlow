package com.example.mainapplication;

public class Order {
    private String orderId;
    private String restaurantName;
    private String details;
    private String status;
    private boolean isPaid;
    private String time;
    private int rating;
    private boolean isRated;
    private String customerName;

    // Constructor
    public Order(String orderId, String restaurantName, String details, String status,
                 boolean isPaid, String time, int rating, boolean isRated, String customerName) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.details = details;
        this.status = status;
        this.isPaid = isPaid;
        this.time = time;
        this.rating = rating;
        this.isRated = isRated;
        this.customerName = customerName;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public String getTime() {
        return time;
    }

    public int getRating() {
        return rating;
    }

    public boolean isRated() {
        return isRated;
    }

    public String getCustomerName() {
        return customerName;
    }

    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setRated(boolean rated) {
        this.isRated = rated;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
