package com.number26;

import com.sun.media.sound.InvalidDataException;
import org.junit.Test;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

/**
 * Created by liviu on 2/5/2016.
 */

public class TransactionStoreTest {

    @Test(expected=NullPointerException.class)
    public void testGetTransactionByIdThrowsNuppPointerExceptionForNullParameters(){
        new TransactionStore().getTransactionById(null);
    }

    @Test
    public void testGetTransactionByTypeReturnsEmptyListForNullParameters(){
        assertTrue("Passing null parameter when getting the transaction by type did not return an empty list ", new TransactionStore().getTransactionByType(null).isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testGetTransactionByIdThrowsNullPointerExceptionForNullParameters(){
        assertNull("Passing null parameter when getting the transaction by id did not return an empty list ", new TransactionStore().getTransactionById(null));
    }
    @Test
    public void testEmptyTransactionStoreReturnsEmptyList(){

        assertTrue("Empty store did not return and empty list", new TransactionStore().getTransactionByType("").isEmpty());
        assertTrue("Empty store did not return null", new TransactionStore().getTransactionById(0L) == null);
    }

    @Test
    public void testStoreCanHoldTransactionsByType() throws InvalidDataException {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertTrue("Store did not hold the first transaction by type", transactionStore.getTransactionByType("first").contains("1"));
        assertTrue("Store did not hold the second transaction by type", transactionStore.getTransactionByType("second").contains("2"));

    }

    @Test
    public void testStoreCanHoldTransactionsById() throws InvalidDataException {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertEquals("Store did not hold the first transaction by id properly", firstTransaction, transactionStore.getTransactionById(1L));
        assertEquals("Store did not hold the second transaction by id properly", secondTransaction, transactionStore.getTransactionById(2L));

    }


    @Test(expected = InvalidDataException.class)
    public void testStoreThrowsExceptionWhenTryingToLoadTheSameIdTwice() throws InvalidDataException {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null, null);
        Transaction secondTransaction = new Transaction(1, "second", null, null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
    }

    @Test()
    public void testStoreHoldsNullTradeTypeAsEmptyString() throws InvalidDataException {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, null, null, null, null);
        Transaction secondTransaction = new Transaction(2, "", null, null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertNotNull("Store did not hold the transaction with null type", transactionStore.getTransactionById(1L));
        assertNotNull("Store did not hold the transaction with empty string type", transactionStore.getTransactionById(2L));

        assertEquals("Store did not hold empty string type for the transaction with null type", "", transactionStore.getTransactionById(1L).getType());
        assertEquals("Store did not hold empty string type for the transaction with empty string type", "", transactionStore.getTransactionById(2L).getType());

    }


    @Test()
    public void testStoreCorrectlyReturnsTransactionsByType() throws InvalidDataException {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null, null);
        Transaction thirdTransaction = new Transaction(3, "first", null, null, null);
        Transaction fourthTransaction = new Transaction(4, "first", null, null, null);
        Transaction fifthTransaction = new Transaction(5, "second", null, null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
        transactionStore.storeTransaction(thirdTransaction);
        transactionStore.storeTransaction(fourthTransaction);
        transactionStore.storeTransaction(fifthTransaction);

        List<String> firstResult = transactionStore.getTransactionByType("first");
        List<String> secondResult = transactionStore.getTransactionByType("second");

        assertFalse("Transaction store did not return any ids for the requested type",
                firstResult.isEmpty());

        assertFalse("Transaction store did not return any ids for the requested type",
                secondResult.isEmpty());

        assertTrue("Transaction store did not return all the ids for the requested type",
                firstResult.contains("1") &&
                        firstResult.contains("3") &&
                        firstResult.contains("4"));

        assertTrue("Transaction store did not return all the ids for the requested type",
                    firstResult.contains("1") &&
                    firstResult.contains("3") &&
                    firstResult.contains("4"));

        assertTrue("Transaction store did not return all the ids for the requested type",
                secondResult.contains("2") &&
                secondResult.contains("5"));

        assertFalse("Transaction store returned too many ids for the requested type",
                firstResult.contains("2") || firstResult.contains("5"));

        assertFalse("Transaction store returned too many ids for the requested type",
                secondResult.contains("1") || secondResult.contains("3") || secondResult.contains("4"));

    }
}
