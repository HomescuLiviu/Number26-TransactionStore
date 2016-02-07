package com.number26;

import com.number26.storage.Transaction;
import com.number26.storage.TransactionStore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;


public class TransactionStoreTest {

    @Test(expected = NullPointerException.class)
    public void testGetTransactionByIdThrowsNuppPointerExceptionForNullParameters() {
        new TransactionStore().getTransactionById(null);
    }

    @Test
    public void testGetTransactionByTypeReturnsEmptyListForNullParameters() {
        assertTrue("Passing null parameter when getting the transaction by type did not return an empty list ", new TransactionStore().getTransactionsByType(null).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testGetTransactionByIdThrowsNullPointerExceptionForNullParameters() {
        assertNull("Passing null parameter when getting the transaction by id did not return an empty list ", new TransactionStore().getTransactionById(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTransactionByIdThrowsExceptionWhenATransactionWithIdDoesNotExist() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction transaction = new Transaction(1, "first", null, BigDecimal.ONE);
        transactionStore.storeTransaction(transaction);
        assertNull("Store returned a transaction that was not added", transactionStore.getTransactionById(3L));
    }

    @Test
    public void testGetTransactionByTypeReturnEmptyListWhenATransactionWithTypeDoesNotExist() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction transaction = new Transaction(1, "first", null, BigDecimal.ONE);
        transactionStore.storeTransaction(transaction);
        assertTrue("Store returned a transaction that was not added", transactionStore.getTransactionsByType("someOtherType").isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAmountByIdThrowsExceptionWhenATransactionWithIdDoesNotExist() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction transaction = new Transaction(1, "first", null, BigDecimal.ONE);
        transactionStore.storeTransaction(transaction);
        assertNull("Store returned an amount that was not added", transactionStore.getAmountById(3L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTransactionByIdThrowsExceptionWhenStoreIsEmpty() {
        assertNull("Empty store returned a transaction by id", new TransactionStore().getTransactionById(3L));
    }

    @Test
    public void testGetTransactionByTypeThrowsNullPointerExceptionWhenStoreIsEmpty() {
        assertTrue("Empty store returned a transaction by type", new TransactionStore().getTransactionsByType("someOtherType").isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAmountByIdThrowsExceptionWhenStoreIsEmpty() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction transaction = new Transaction(1, "first", null, BigDecimal.ONE);
        transactionStore.storeTransaction(transaction);
        assertNull("Empty store returned an amount by id", new TransactionStore().getAmountById(3L));
    }

    @Test
    public void testStoreCanHoldTransactionsByType() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(2, "second", null, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertTrue("Store did not hold the first transaction by type", transactionStore.getTransactionsByType("first").contains("1"));
        assertTrue("Store did not hold the second transaction by type", transactionStore.getTransactionsByType("second").contains("2"));
    }

    @Test
    public void testStoreCanReturnTransactionsById() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(2, "second", null, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertEquals("Store did not hold the first transaction by id properly", firstTransaction, transactionStore.getTransactionById(1L));
        assertEquals("Store did not hold the second transaction by id properly", secondTransaction, transactionStore.getTransactionById(2L));

    }


    @Test(expected = IllegalArgumentException.class)
    public void testStoreCanLoadTheSameIdTwice() {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(1, "second", null, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
    }

    @Test()
    public void testStoreHoldsNullTradeTypeAsEmptyString() {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, null, null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(2, "", null, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertNotNull("Store did not hold the transaction with null type", transactionStore.getTransactionById(1L));
        assertNotNull("Store did not hold the transaction with empty string type", transactionStore.getTransactionById(2L));

        assertEquals("Store did not hold empty string type for the transaction with null type", "", transactionStore.getTransactionById(1L).getType());
        assertEquals("Store did not hold empty string type for the transaction with empty string type", "", transactionStore.getTransactionById(2L).getType());

    }


    @Test()
    public void testStoreCorrectlyReturnsTransactionsByType() {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(2, "second", null, BigDecimal.ONE);
        Transaction thirdTransaction = new Transaction(3, "first", null, BigDecimal.ONE);
        Transaction fourthTransaction = new Transaction(4, "first", null, BigDecimal.ONE);
        Transaction fifthTransaction = new Transaction(5, "second", null, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
        transactionStore.storeTransaction(thirdTransaction);
        transactionStore.storeTransaction(fourthTransaction);
        transactionStore.storeTransaction(fifthTransaction);

        List<String> firstResult = transactionStore.getTransactionsByType("first");
        List<String> secondResult = transactionStore.getTransactionsByType("second");

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

    @Test
    public void testStoreReturnsInitialAmountIfThereAreNoChildTransactions() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.valueOf(2.334));

        transactionStore.storeTransaction(firstTransaction);
        BigDecimal amount = transactionStore.getAmountById(1L);

        assertEquals("Store did not return the correct total amount", BigDecimal.valueOf(2.334), amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreThrowsExceptionWhenTheParentIdDoesNotExist() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.ONE);
        Transaction secondTransaction = new Transaction(2, "second", 3L, BigDecimal.ONE);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

    }

    @Test
    public void testStoreCorrectlyReturnsAmountForTransactionId() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.valueOf(2.334));
        Transaction secondTransaction = new Transaction(2, "second", 1L, BigDecimal.valueOf(3.116));
        Transaction thirdTransaction = new Transaction(3, "third", 2L, BigDecimal.valueOf(-2.5));
        Transaction fourthTransaction = new Transaction(4, "fourth", 2L, BigDecimal.valueOf(4.5));

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
        transactionStore.storeTransaction(thirdTransaction);
        transactionStore.storeTransaction(fourthTransaction);

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Store did not return the correct amount for the first transaction", BigDecimal.valueOf(7.45), transactionStore.getAmountById(1L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the second transaction", BigDecimal.valueOf(5.116), transactionStore.getAmountById(2L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the third transaction", BigDecimal.valueOf(-2.5), transactionStore.getAmountById(3L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the fourth transaction", BigDecimal.valueOf(4.5), transactionStore.getAmountById(4L).stripTrailingZeros());
    }

    @Test
    public void testStoreCorrectlyReturnsAmountForTransactionIdWhenRequestsAreParallel() throws InterruptedException {
        AtomicLong childNumber = new AtomicLong(5);
        int numberOfThreads = 10 * 1000;

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.valueOf(2.334));
        Transaction secondTransaction = new Transaction(2, "second", 1L, BigDecimal.valueOf(3.116));
        Transaction thirdTransaction = new Transaction(3, "third", 2L, BigDecimal.valueOf(-2.5));
        Transaction fourthTransaction = new Transaction(4, "fourth", 2L, BigDecimal.valueOf(4.5));

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
        transactionStore.storeTransaction(thirdTransaction);
        transactionStore.storeTransaction(fourthTransaction);

        ExecutorService executorService = new ScheduledThreadPoolExecutor(100);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> addFourTransationsToTheStore(childNumber, transactionStore));
        }

        try {
            Thread.sleep(120 * 1000);
        } catch (InterruptedException e) {
            executorService.shutdown();
            e.printStackTrace();
            throw e;
        }
        executorService.shutdown();
        assertEquals("Store did not return the correct amount for the first transaction", BigDecimal.valueOf(5.116 * numberOfThreads + 7.45), transactionStore.getAmountById(1L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the second transaction", BigDecimal.valueOf(2 * numberOfThreads + 5.116), transactionStore.getAmountById(2L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the third transaction", BigDecimal.valueOf(-2.5), transactionStore.getAmountById(3L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the fourth transaction", BigDecimal.valueOf(4.5), transactionStore.getAmountById(4L).stripTrailingZeros());

    }


    @Test
    public void testStoreCorrectlyReturnsAmountForTransactionIdWhenTransactionsAreLinkedToEeachother() throws InterruptedException {
        AtomicLong childNumber = new AtomicLong(5);
        AtomicLong previousChildNumber = new AtomicLong(4);
        int numberOfTransactions = 1000;

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, BigDecimal.valueOf(2.334));
        Transaction secondTransaction = new Transaction(2, "second", 1L, BigDecimal.valueOf(3.116));
        Transaction thirdTransaction = new Transaction(3, "third", 2L, BigDecimal.valueOf(-2.5));
        Transaction fourthTransaction = new Transaction(4, "fourth", 2L, BigDecimal.valueOf(4.5));

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
        transactionStore.storeTransaction(thirdTransaction);
        transactionStore.storeTransaction(fourthTransaction);

        for (int i = 0; i < numberOfTransactions; i++) {
            addFourTransationsToTheStoreWithEachotherAsParent(childNumber, previousChildNumber, transactionStore);
        }

        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }

        assertEquals("Store did not return the correct amount for the first transaction", BigDecimal.valueOf(5.116 * numberOfTransactions + 7.45), transactionStore.getAmountById(1L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the second transaction", BigDecimal.valueOf(5.116 * numberOfTransactions + 5.116), transactionStore.getAmountById(2L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the third transaction", BigDecimal.valueOf(-2.5), transactionStore.getAmountById(3L).stripTrailingZeros());
        assertEquals("Store did not return the correct amount for the fourth transaction", BigDecimal.valueOf(5.116 * numberOfTransactions + 4.5), transactionStore.getAmountById(4L).stripTrailingZeros());

    }

    private void addFourTransationsToTheStoreWithEachotherAsParent(AtomicLong childNumber, AtomicLong previousChildNumber, TransactionStore transactionStore) {
        Transaction secondChildTransaction = new Transaction(childNumber.getAndIncrement(), "second", previousChildNumber.getAndIncrement(), BigDecimal.valueOf(3.116));
        Transaction thirdChildTransaction = new Transaction(childNumber.getAndIncrement(), "third", previousChildNumber.getAndIncrement(), BigDecimal.valueOf(-2.5));
        Transaction fourthChildTransaction = new Transaction(childNumber.getAndIncrement(), "fourth", previousChildNumber.getAndIncrement(), BigDecimal.valueOf(4.5));

        transactionStore.storeTransaction(secondChildTransaction);
        transactionStore.storeTransaction(thirdChildTransaction);
        transactionStore.storeTransaction(fourthChildTransaction);
    }

    private void addFourTransationsToTheStore(AtomicLong childNumber, TransactionStore transactionStore) {
        Transaction secondChildTransaction = new Transaction(childNumber.getAndIncrement(), "second", 1L, BigDecimal.valueOf(3.116));
        Transaction thirdChildTransaction = new Transaction(childNumber.getAndIncrement(), "third", 2L, BigDecimal.valueOf(-2.5));
        Transaction fourthChildTransaction = new Transaction(childNumber.getAndIncrement(), "fourth", 2L, BigDecimal.valueOf(4.5));

        transactionStore.storeTransaction(secondChildTransaction);
        transactionStore.storeTransaction(thirdChildTransaction);
        transactionStore.storeTransaction(fourthChildTransaction);
    }

}
