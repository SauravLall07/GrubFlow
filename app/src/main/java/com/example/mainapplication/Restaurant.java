package com.example.mainapplication;

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private double rating;
    private int reviewCount;
    private String address;
    private String phoneNumber;
    private String openingHours;
    private boolean isOpen;
    private String distance;

    public Restaurant(int id, String name, String description, String imageUrl,
                      double rating, int reviewCount, String address,
                      String phoneNumber, String openingHours, boolean isOpen,
                      String distance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.openingHours = openingHours;
        this.isOpen = isOpen;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getDistance() {
        return distance;
    }
}