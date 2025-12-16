package com.acn4bv.buglog;

public class User {
    private String id;
    private String email;
    private String displayName;
    private String role; // "admin" o "tester"
    private long createdAt;

    public User() {
        // Constructor vacío requerido por Firestore
    }

    public User(String email, String displayName, String role) {
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}