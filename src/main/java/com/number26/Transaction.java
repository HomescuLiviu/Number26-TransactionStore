package com.number26;

import java.math.BigDecimal;


public class Transaction {

    private long id;
    private String type;
    private Long parentId;
    private BigDecimal amount;
    private BigDecimal totalAmount;

    public Transaction(long id, String type, Long parentId, BigDecimal amount, BigDecimal totalAmount) {
        this.id = id;
        this.type = type;
        this.parentId = parentId;
        this.amount = amount;
        this.totalAmount = totalAmount;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public long getParentId() {
        return parentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public synchronized void addTotalAmount(BigDecimal amountToAdd) {
        this.totalAmount = totalAmount.add(amountToAdd);
    }
}
