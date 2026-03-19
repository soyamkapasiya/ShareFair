package com.kapasiya.sharefair.dtos;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private long createdAt;
    private long lastActiveAt;
    private Map<String, UserGroup> groups;
    private double totalBalance;
    private boolean isActive;

    public User() {
        this.groups = new HashMap<>();
        this.totalBalance = 0.0;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = System.currentTimeMillis();
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

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(long lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public Map<String, UserGroup> getGroups() { return groups; }
    public void setGroups(Map<String, UserGroup> groups) { this.groups = groups; }

    public double getTotalBalance() { return totalBalance; }
    public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Helper methods
    public void addGroup(String groupId, UserGroup userGroup) {
        if (this.groups == null) {
            this.groups = new HashMap<>();
        }
        this.groups.put(groupId, userGroup);
    }

    public void removeGroup(String groupId) {
        if (this.groups != null) {
            this.groups.remove(groupId);
        }
    }

    public UserGroup getGroup(String groupId) {
        if (this.groups != null) {
            return this.groups.get(groupId);
        }
        return null;
    }

    public int getGroupCount() {
        return this.groups != null ? this.groups.size() : 0;
    }
}