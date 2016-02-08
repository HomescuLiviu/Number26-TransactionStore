package com.number26.servlets;

import com.number26.storage.TransactionStore;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TransactionAmountServletTest {

    private TransactionStore transactionStoreMock = mock(TransactionStore.class);
    private PrintWriter writerMock = mock(PrintWriter.class);
    private HttpServletRequest requestMock = mock(HttpServletRequest.class);
    private HttpServletResponse responseMock = mock(HttpServletResponse.class);
    private TransactionAmountServlet TransactionAmountServlet = new TransactionAmountServlet(transactionStoreMock);

    @Before
    public void setup() throws IOException {
        transactionStoreMock = mock(TransactionStore.class);
        TransactionAmountServlet = new TransactionAmountServlet(transactionStoreMock);
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);

        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    public void testGettingTransactionByIdReturnsListOfErrorsWhenStoreReturnsErrors() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(transactionStoreMock.getAmountByTransactionId(anyLong())).thenThrow(new IllegalArgumentException("Test exception"));
        TransactionAmountServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append("{\"errors\":\"Test exception\"}");
    }

    @Test
    public void testCanGetSumOfMultipleTransactions() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);
        TransactionAmountServlet localTransactionAmountServlet = new TransactionAmountServlet(transactionStore);
        HttpServletRequest secondRequestMock = mock(HttpServletRequest.class);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");

        when(requestMock.getParameter("parent_id")).thenReturn(null);
        when(requestMock.getParameter("amount")).thenReturn("2.3");
        when(requestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(writerMock, times(1)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);

        when(secondRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2");
        when(secondRequestMock.getParameter("parent_id")).thenReturn("1");
        when(secondRequestMock.getParameter("amount")).thenReturn("2.3");
        when(secondRequestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(secondRequestMock, responseMock);

        verify(writerMock, times(2)).append("{\"status\":\"ok\"}");
        verify(responseMock, times(2)).setStatus(HttpServletResponse.SC_OK);


        localTransactionAmountServlet.doGet(requestMock, responseMock);
        localTransactionAmountServlet.doGet(secondRequestMock, responseMock);

        verify(writerMock, times(1)).append("{\"sum\":4.6}");
    }
}