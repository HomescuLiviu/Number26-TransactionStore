package com.number26;

import com.sun.media.sound.InvalidDataException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by liviu on 2/5/2016.
 */

public class TransactionStoreTest {

    @Test(expected=NullPointerException.class)
    public void testGetTransactionByIdThrowsNuppPointerExceptionForNullParameters(){
        new TransactionStore().getTransactionById(null);
    }

    @Test(expected=NullPointerException.class)
    public void testGetTransactionByTypeThrowsNuppPointerExceptionForNullParameters(){
        new TransactionStore().getTransactionByType(null);
    }

    @Test
    public void testEmptyTransactionStoreReturnsEmptyList(){

        assertTrue("Empty store did not return and empty list", new TransactionStore().getTransactionByType("").isEmpty());
        assertTrue("Empty store did not return null", new TransactionStore().getTransactionById(0L) == null);
    }

    @Test
    public void testStoreCanHoldTransactionsByType() throws InvalidDataException {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction();
        Transaction secondTransaction = new Transaction();

        firstTransaction.setId(1);
        firstTransaction.setType("first");

        secondTransaction.setId(2);
        secondTransaction.setType("second");

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertTrue("Store did not hold the first transaction by type", transactionStore.getTransactionByType("first").contains(1L));
        assertTrue("Store did not hold the second transaction by type", transactionStore.getTransactionByType("second").contains(2L));

    }

    @Test
    public void testStoreCanHoldTransactionsById() throws InvalidDataException {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction();
        Transaction secondTransaction = new Transaction();

        firstTransaction.setId(1);
        firstTransaction.setType("first");

        secondTransaction.setId(2);
        secondTransaction.setType("second");

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertTrue("Store did not hold the first transaction by id", transactionStore.getTransactionByType("first").contains(1L));
        assertTrue("Store did not hold the second transaction by id", transactionStore.getTransactionByType("second").contains(2L));

    }


    @Test(expected = InvalidDataException.class)
    public void testStoreThrowsExceptionWhenTryingToLoadTheSameIdTwice() throws InvalidDataException {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction();
        Transaction secondTransaction = new Transaction();

        firstTransaction.setId(1);
        firstTransaction.setType("first");

        secondTransaction.setId(1);
        secondTransaction.setType("second");

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
    }

    @Test()
    public void testStoreHoldsNullTradeTypeAsEmptyString() throws InvalidDataException {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction();
        Transaction secondTransaction = new Transaction();

        firstTransaction.setId(1);
        firstTransaction.setType(null);

        secondTransaction.setId(2);
        secondTransaction.setType("");

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertNotNull("Store did not hold the transaction with null type", transactionStore.getTransactionById(1L));
        assertNotNull("Store did not hold the transaction with empty string type", transactionStore.getTransactionById(2L));

        assertEquals("Store did not hold empty string type for the transaction with null type", "", transactionStore.getTransactionById(1L).getType());
        assertEquals("Store did not hold empty string type for the transaction with empty string type", "", transactionStore.getTransactionById(2L).getType());

    }
}
