package com.kapasiya.sharefair.dtos;

public class GroupMember {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role; // "admin", "member"
    private long joinedAt;
    private double totalPaid;
    private double totalOwed;
    private boolean isActive;

    public GroupMember() {
        this.totalPaid = 0.0;
        this.totalOwed = 0.0;
        this.isActive = true;
        this.role = "member";
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getJoinedAt() { return joinedAt; }
    public void setJoinedAt(long joinedAt) { this.joinedAt = joinedAt; }

    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }

    public double getTotalOwed() { return totalOwed; }
    public void setTotalOwed(double totalOwed) { this.totalOwed = totalOwed; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
