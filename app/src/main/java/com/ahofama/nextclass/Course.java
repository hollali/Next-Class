package com.ahofama.nextclass;

public class Course {
    private String title;
    private String instructor;
    private String thumbnailUrl;

    // Empty constructor for Firestore
    public Course() { }

    public Course(String title, String instructor, String thumbnailUrl) {
        this.title = title;
        this.instructor = instructor;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
