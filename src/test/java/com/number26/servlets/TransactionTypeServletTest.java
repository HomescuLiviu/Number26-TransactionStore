package com.number26.servlets;

import com.number26.storage.TransactionStore;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;


public class TransactionTypeServletTest {
    private static final String TRANSACTION_TYPE_ERROR = "{\"errors\":\"Error when trying to find transactions with type {%s} : {%s}\"}";

    private TransactionStore transactionStoreMock = mock(TransactionStore.class);
    private PrintWriter writerMock = mock(PrintWriter.class);
    private HttpServletRequest requestMock = mock(HttpServletRequest.class);
    private HttpServletResponse responseMock = mock(HttpServletResponse.class);
    private TransactionTypeServlet transactionTypeServlet = new TransactionTypeServlet(transactionStoreMock);

    @Before
    public void setup() throws IOException {
        transactionStoreMock = mock(TransactionStore.class);
        transactionTypeServlet = new TransactionTypeServlet(transactionStoreMock);
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);

        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    public void testGettingTransactionByTypeReturnsListOfErrorsWhenStoreReturnsErrors() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/types/testtype");
        when(transactionStoreMock.getTransactionsByType(anyString())).thenThrow(new IllegalArgumentException("Test exception"));

        transactionTypeServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(TRANSACTION_TYPE_ERROR, "testtype", "Test exception"));
    }

    @Test
    public void testGettingTransactionByTypeReturnsEmptyListWhenThereArentTransactionOfThatType() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/types/testtype");
        when(transactionStoreMock.getTransactionsByType("testtype")).thenReturn(new ArrayList<>());

        transactionTypeServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("[");
        verify(writerMock, times(1)).append("]");
        verify(writerMock, times(2)).append(anyString());
    }

    @Test
    public void testCanGetMultipleTransactiosWithTheSameId() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);
        TransactionTypeServlet localTransactionTypeServlet = new TransactionTypeServlet(transactionStore);
        HttpServletRequest firstPutRequestMock = mock(HttpServletRequest.class);
        HttpServletRequest secondPutRequestMock = mock(HttpServletRequest.class);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/types/someType");
        when(firstPutRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");

        when(firstPutRequestMock.getParameter("parent_id")).thenReturn(null);
        when(firstPutRequestMock.getParameter("amount")).thenReturn("2.3");
        when(firstPutRequestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(firstPutRequestMock, responseMock);

        verify(writerMock, times(1)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(writerMock, times(1)).append("{\"status\":\"ok\"}");

        when(secondPutRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2");
        when(secondPutRequestMock.getParameter("parent_id")).thenReturn("1");
        when(secondPutRequestMock.getParameter("amount")).thenReturn("2.3");
        when(secondPutRequestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(secondPutRequestMock, responseMock);

        verify(writerMock, times(2)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(2)).setStatus(HttpServletResponse.SC_OK);
        verify(responseMock, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        localTransactionTypeServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("[");
        verify(writerMock, times(1)).append("1,2");
        verify(writerMock, times(1)).append("]");
        verify(writerMock, times(5)).append(anyString()); // 3 times for the [1,2] and twice for the ok responses

    }

    @Test
    public void testTransactionsOfTypeNullAreSameAsOfTypeEmpty() throws ServletException, IOException {

        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);
        TransactionTypeServlet localTransactionTypeServlet = new TransactionTypeServlet(transactionStore);
        HttpServletRequest firstPutRequestMock = mock(HttpServletRequest.class);
        HttpServletRequest secondPutRequestMock = mock(HttpServletRequest.class);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/types/");
        when(firstPutRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");

        when(firstPutRequestMock.getParameter("parent_id")).thenReturn(null);
        when(firstPutRequestMock.getParameter("amount")).thenReturn("2.3");
        when(firstPutRequestMock.getParameter("type")).thenReturn("");

        localTransactionServiceServlet.doPut(firstPutRequestMock, responseMock);

        verify(writerMock, times(1)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(writerMock, times(1)).append("{\"status\":\"ok\"}");

        when(secondPutRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2");
        when(secondPutRequestMock.getParameter("parent_id")).thenReturn("1");
        when(secondPutRequestMock.getParameter("amount")).thenReturn("2.3");
        when(secondPutRequestMock.getParameter("type")).thenReturn(null);

        localTransactionServiceServlet.doPut(secondPutRequestMock, responseMock);

        verify(writerMock, times(2)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(2)).setStatus(HttpServletResponse.SC_OK);
        verify(responseMock, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        localTransactionTypeServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("[");
        verify(writerMock, times(1)).append("1,2");
        verify(writerMock, times(1)).append("]");
        verify(writerMock, times(5)).append(anyString());

    }

}
