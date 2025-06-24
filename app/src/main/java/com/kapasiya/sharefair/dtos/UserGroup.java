package com.kapasiya.sharefair.dtos;

public class UserGroup {
    private String groupId;
    private String groupName;
    private String groupType;
    private String role;
    private long joinedAt;
    private double totalPaid;
    private double totalOwed;
    private boolean isActive;

    public UserGroup() {
        this.totalPaid = 0.0;
        this.totalOwed = 0.0;
        this.isActive = true;
        this.role = "member";
    }

    // Getters and Setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

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
