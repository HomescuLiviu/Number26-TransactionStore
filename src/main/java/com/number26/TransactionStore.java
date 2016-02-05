package com.number26;

import com.sun.media.sound.InvalidDataException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TransactionStore {

    private ConcurrentHashMap<Long, Transaction> store = new ConcurrentHashMap<Long, Transaction>();
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Long>> typeStore = new ConcurrentHashMap<String, CopyOnWriteArrayList<Long>>();

    public List<Long> getTransactionByType(String type){
        return typeStore.get(type);
    }

    public Transaction getTransactionById(Long id){
        return store.get(id);
    }

    public void storeTransaction(Transaction transaction) throws InvalidDataException {
        if (store.get(transaction.getId())!= null) {
            throw new InvalidDataException("Already have a transaction with id :" + transaction.getId());
        }
        transaction.setTotalAmount(transaction.getAmount());
        store.put(transaction.getId(), transaction);
        updateTypeStoreWithTransactionData(transaction);
        addAmountToAllAncestors(transaction);
    }

    private void updateTypeStoreWithTransactionData(Transaction transaction) {
        List<Long> idListByType = typeStore.get(transaction.getType()) == null ? new CopyOnWriteArrayList<Long>() : typeStore.get(transaction.getType());
        idListByType.add(transaction.getId());
        typeStore.put(transaction.getType(), (CopyOnWriteArrayList<Long>) idListByType);
    }

    private void addAmountToAllAncestors(Transaction transaction) {
        Transaction parent = store.get(transaction.getParentId());
        while (parent != null){
            parent.setTotalAmount(transaction.getAmount().add(parent.getTotalAmount()));
            parent = store.get(parent.getParentId());
        }
    }
}
