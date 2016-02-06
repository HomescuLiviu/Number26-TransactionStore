package com.number26;

import com.sun.media.sound.InvalidDataException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionStore {

    private ConcurrentHashMap<Long, Transaction> store = new ConcurrentHashMap<Long, Transaction>();

    public List<String> getTransactionByType(String type){
        return store.values()
                .parallelStream()
                .filter((t) -> t.getType().equals(type))
                .map((t) -> String.valueOf(t.getId()))
                .collect(Collectors.toList());
    }

    public Transaction getTransactionById(Long id){
        return store.get(id);
    }

    public void storeTransaction(Transaction transaction) throws InvalidDataException {
        if (store.get(transaction.getId())!= null) {
            throw new InvalidDataException("Already have a transaction with id :" + transaction.getId());
        }
        store.put(transaction.getId(), transaction);
    }



}
