package com.kapasiya.sharefair.dtos;

public class BillItems {
    private String billDate;
    private String billTitle;
    private String billAmountPaid;
    private String billPayBy;
    private String billPayAmount;

    // Constructor
    public BillItems(String billDate, String billTitle, String billAmountPaid, String billPayBy, String billPayAmount) {
        this.billDate = billDate;
        this.billTitle = billTitle;
        this.billAmountPaid = billAmountPaid;
        this.billPayBy = billPayBy;
        this.billPayAmount = billPayAmount;
    }

    // Getters
    public String getBillDate() {
        return billDate;
    }

    public String getBillTitle() {
        return billTitle;
    }

    public String getBillAmountPaid() {
        return billAmountPaid;
    }

    public String getBillPayBy() {
        return billPayBy;
    }

    public String getBillPayAmount() {
        return billPayAmount;
    }

    // Setters
    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public void setBillTitle(String billTitle) {
        this.billTitle = billTitle;
    }

    public void setBillAmountPaid(String billAmountPaid) {
        this.billAmountPaid = billAmountPaid;
    }

    public void setBillPayBy(String billPayBy) {
        this.billPayBy = billPayBy;
    }

    public void setBillPayAmount(String billPayAmount) {
        this.billPayAmount = billPayAmount;
    }
}