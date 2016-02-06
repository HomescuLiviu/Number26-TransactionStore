package com.number26;

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
        assertTrue("Passing null parameter when getting the transaction by type did not return an empty list ", new TransactionStore().getTransactionByType(null).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testGetTransactionByIdThrowsNullPointerExceptionForNullParameters() {
        assertNull("Passing null parameter when getting the transaction by id did not return an empty list ", new TransactionStore().getTransactionById(null));
    }

    @Test
    public void testEmptyTransactionStoreReturnsEmptyList() {

        assertTrue("Empty store did not return and empty list", new TransactionStore().getTransactionByType("").isEmpty());
        assertTrue("Empty store did not return null", new TransactionStore().getTransactionById(0L) == null);
    }

    @Test
    public void testStoreCanHoldTransactionsByType() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertTrue("Store did not hold the first transaction by type", transactionStore.getTransactionByType("first").contains("1"));
        assertTrue("Store did not hold the second transaction by type", transactionStore.getTransactionByType("second").contains("2"));
    }

    @Test
    public void testStoreCanReturnTransactionsById() {
        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);

        assertEquals("Store did not hold the first transaction by id properly", firstTransaction, transactionStore.getTransactionById(1L));
        assertEquals("Store did not hold the second transaction by id properly", secondTransaction, transactionStore.getTransactionById(2L));

    }


    @Test(expected = IllegalArgumentException.class)
    public void testStoreThrowsExceptionWhenTryingToLoadTheSameIdTwice() {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, "first", null, null);
        Transaction secondTransaction = new Transaction(1, "second", null, null);

        transactionStore.storeTransaction(firstTransaction);
        transactionStore.storeTransaction(secondTransaction);
    }

    @Test()
    public void testStoreHoldsNullTradeTypeAsEmptyString() {

        TransactionStore transactionStore = new TransactionStore();
        Transaction firstTransaction = new Transaction(1, null, null, null);
        Transaction secondTransaction = new Transaction(2, "", null, null);

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
        Transaction firstTransaction = new Transaction(1, "first", null, null);
        Transaction secondTransaction = new Transaction(2, "second", null, null);
        Transaction thirdTransaction = new Transaction(3, "first", null, null);
        Transaction fourthTransaction = new Transaction(4, "first", null, null);
        Transaction fifthTransaction = new Transaction(5, "second", null, null);

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
        Transaction firstTransaction = new Transaction(1, "first", null, null);
        Transaction secondTransaction = new Transaction(2, "second", 3L, null);

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
            Thread.sleep(60 * 1000);
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
    public void testStoreCorrectlyReturnsAmountForTransactionIdWhenParralelRequestsAreLinkedToEeachother() throws InterruptedException {
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
            executorService.submit(() -> addFourTransationsToTheStoreWithEachotherAsParent(childNumber, transactionStore));
        }

        try {
            Thread.sleep(60 * 1000);
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

    private void addFourTransationsToTheStoreWithEachotherAsParent(AtomicLong childNumber, TransactionStore transactionStore) {
        Long previousChild = childNumber.get();
        Transaction secondChildTransaction = new Transaction(childNumber.getAndIncrement(), "second", previousChild, BigDecimal.valueOf(3.116));
        Transaction thirdChildTransaction = new Transaction(childNumber.getAndIncrement(), "third", previousChild, BigDecimal.valueOf(-2.5));
        Transaction fourthChildTransaction = new Transaction(childNumber.getAndIncrement(), "fourth", previousChild, BigDecimal.valueOf(4.5));

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
