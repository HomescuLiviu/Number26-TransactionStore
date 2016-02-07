package com.number26.storage;

import java.math.BigDecimal;
import java.util.Optional;


public class Transaction {

    private long id;
    private String type;
    private Optional<Long> parentId;
    private BigDecimal amount;
    private BigDecimal totalAmount;

    public Transaction(long id, String type, Long parentId, BigDecimal amount) {
        this.id = id;
        this.type = type == null ? "" : type;
        this.parentId = Optional.ofNullable(parentId);
        this.amount = amount;
        this.totalAmount = amount;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Optional<Long> getParentId() {
        return parentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public synchronized void addTotalAmount(BigDecimal amountToAdd) {
        this.totalAmount = totalAmount.add(amountToAdd);
    }
}
