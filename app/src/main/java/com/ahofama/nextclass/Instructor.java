package com.ahofama.nextclass;

public class Instructor {
    private String name;
    private String subject;
    private String specialization;
    private String experience;
    private float rating;
    private double hourlyRate;
    private String status;
    private boolean isAvailable;
    private String[] tags;
    private String imageUrl; // For profile picture

    public Instructor(String name, String subject, String specialization, String experience,
                      float rating, double hourlyRate, String status, boolean isAvailable, String[] tags) {
        this.name = name;
        this.subject = subject;
        this.specialization = specialization;
        this.experience = experience;
        this.rating = rating;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.isAvailable = isAvailable;
        this.tags = tags;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getExperience() {
        return experience;
    }

    public float getRating() {
        return rating;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String[] getTags() {
        return tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Helper methods
    public String getFormattedRate() {
        return String.format("$%.0f/hour", hourlyRate);
    }

    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }
}