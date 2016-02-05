package com.number26;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liviu on 2/5/2016.
 */
public class TransactionStore {

    private Map<Long, Transaction> store = new ConcurrentHashMap();
    private Map<String, CopyOnWriteArrayList> typeStore = new ConcurrentHashMap();

    public List<Long> getTransactionByType(String type){
        return typeStore.get(type);
    }

    public void storeTransaction(Transaction transaction){
        store.put(transaction.getId(), transaction);
        List idListByType = typeStore.get(transaction.getType()) == null ? new CopyOnWriteArrayList<Long>() : typeStore.get(transaction.getType());
        idListByType.add(transaction.getId());
        typeStore.put(transaction.getType(), (CopyOnWriteArrayList) idListByType);
    }
}
