package com.example.mainapplication;

public class Order {
    private String orderId;
    private String restaurantName;
    private String details; // Items like "Burger (2), Chips (1)"
    private String status;
    private boolean isPaid;
    private String time;   // order_date
    private int rating;
    private boolean isRated;
    private String customerName;

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

    // Add getters and setters for all fields, for example:
    public String getOrderId() { return orderId; }
    public String getRestaurantName() { return restaurantName; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public boolean isPaid() { return isPaid; }
    public String getTime() { return time; }
    public int getRating() { return rating; }
    public boolean isRated() { return isRated; }
    public String getCustomerName() { return customerName; }

    public void setRated(boolean rated) { this.isRated = rated; }
    public void setRating(int rating) { this.rating = rating; }

    // Add any other convenience methods if needed
}
