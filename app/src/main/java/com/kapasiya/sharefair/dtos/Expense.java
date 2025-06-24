package com.kapasiya.sharefair.dtos;

import java.util.Map;

public class Expense {
    private String expenseId;
    private String groupId;
    private String description;
    private double amount;
    private String paidBy;
    private String paidByName;
    private long createdAt;
    private Map<String, Double> splitAmong; // userId -> amount owed
    private String category;
    private boolean isSettled;

    public Expense() {
        this.isSettled = false;
    }

    // Getters and Setters
    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }

    public String getPaidByName() { return paidByName; }
    public void setPaidByName(String paidByName) { this.paidByName = paidByName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Double> getSplitAmong() { return splitAmong; }
    public void setSplitAmong(Map<String, Double> splitAmong) { this.splitAmong = splitAmong; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isSettled() { return isSettled; }
    public void setSettled(boolean settled) { isSettled = settled; }
}
