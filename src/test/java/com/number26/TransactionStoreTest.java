package com.number26;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by liviu on 2/5/2016.
 */

public class TransactionStoreTest {

    @Test
    public void testEmptyTransactionStoreReturnsEmptyList(){

        assertTrue("Empty store did not return and empty list", new TransactionStore().getTransactionByType("").isEmpty());
    }

    @Test
    public void testStoreCanHoldTransactionsByType(){
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction();
        Transaction secondTransaction = new Transaction();

        firstTransaction.setId(1);
        firstTransaction.setType("first");

        secondTransaction.setId(2);
        secondTransaction.setType("second");

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertEquals("Store did not hold the first transaction", 1, transactionStore.getTransactionByType("first"));
        assertEquals("Store did not hold the second transaction", 2, transactionStore.getTransactionByType("second"));

    }
}
