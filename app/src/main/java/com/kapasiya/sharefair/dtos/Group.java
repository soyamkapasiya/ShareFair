package com.kapasiya.sharefair.dtos;


import java.util.HashMap;
import java.util.Map;

public class Group {
    private String groupId;
    private String groupName;
    private String groupType;
    private String createdBy;
    private long createdAt;
    private Map<String, GroupMember> members;
    private Map<String, Expense> expenses;
    private double totalExpenses;
    private boolean isActive;

    public Group() {
        this.members = new HashMap<>();
        this.expenses = new HashMap<>();
        this.totalExpenses = 0.0;
        this.isActive = true;
    }

    // Getters and Setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, GroupMember> getMembers() { return members; }
    public void setMembers(Map<String, GroupMember> members) { this.members = members; }

    public Map<String, Expense> getExpenses() { return expenses; }
    public void setExpenses(Map<String, Expense> expenses) { this.expenses = expenses; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
