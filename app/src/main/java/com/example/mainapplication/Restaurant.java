package com.example.mainapplication;

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private double rating;
    private int reviewCount;
    private String location;
    private String contact;
    private String openingHours;
    private boolean isOpen;
    private String distance;

    public Restaurant(int id, String name, String description, String imageUrl,
                      double rating, int reviewCount, String location,
                      String contact, String openingHours, boolean isOpen,
                      String distance) {
        this.id = id;
        this.name = name;
        this.description = ""; // default
        this.imageUrl = "";    // default
        this.rating = 0.0;
        this.reviewCount = 0;
        this.location = location;
        this.contact = contact;
        this.openingHours = "";
        this.isOpen = true;
        this.distance = "";
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

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
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