package com.example.issah.models;

import com.google.firebase.Timestamp;
import java.util.Date;

public class KumbukaItem {
    private String title;
    private String content;
    private String type;
    private Object timestamp;  // Can be either Long or Timestamp
    private String userId;
    private String userEmail;
    private String imageUrl;

    // Empty constructor needed for Firestore
    public KumbukaItem() {}

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Modified getter/setter for timestamp
    public Object getTimestamp() { return timestamp; }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    // Helper method to get timestamp as long
    public long getTimestampMillis() {
        if (timestamp instanceof Long) {
            return (Long) timestamp;
        } else if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toDate().getTime();
        }
        return 0;
    }

    // Helper method to get as Date
    public Date getTimestampDate() {
        if (timestamp instanceof Long) {
            return new Date((Long) timestamp);
        } else if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toDate();
        }
        return null;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}