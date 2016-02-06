package com.number26;

import com.google.common.base.Joiner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TransactionStore {

    private ConcurrentHashMap<Long, Transaction> store = new ConcurrentHashMap<>();

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

    public void storeTransaction(Transaction transaction) throws IllegalArgumentException {
        System.out.println(String.format("Adding transaction id {%d}", transaction.getId()));
        validateTransaction(transaction);
        store.put(transaction.getId(), transaction);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> updateTransactionWithTotalAmount(transaction));

        System.out.println(String.format("Transaction id {%s}", transaction.getId() + " added"));
    }

    private void validateTransaction(Transaction transaction) {

        // add transaction validators here

        ArrayList<String> exceptions = new ArrayList<>();

        if (store.get(transaction.getId())!= null) {
            exceptions.add(String.format("Already have a transaction with id : {%s}", transaction.getId()));
        }

        if (transaction.getAmount()== null) {
            exceptions.add("Transaction with id {%s} does not have an amount" + transaction.getId());
        }

        if (transaction.getParentId().isPresent() && store.get(transaction.getParentId().get()) == null) {
            exceptions.add(String.format("Transaction with id {%s} has  parent id {%s} which doens not exist ", transaction.getId(), transaction.getParentId()));
        }
       if ( !exceptions.isEmpty() ){
           throw new IllegalArgumentException(Joiner.on("\t").join(exceptions));
       }
    }

    /*
    Failing to update the total amounts should not stop the booking of a transaction.
    The errors should be later handled by Production Support
    */

    private void updateTransactionWithTotalAmount(Transaction transaction) {
        try {
            BigDecimal amountToAdd = transaction.getAmount();
            while (transaction.getParentId().isPresent()) {
                transaction = getTransactionById(transaction.getParentId().get());
                transaction.addTotalAmount(amountToAdd);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public BigDecimal getAmountById(Long i) {
          return store.get(i).getTotalAmount();
    }
}
