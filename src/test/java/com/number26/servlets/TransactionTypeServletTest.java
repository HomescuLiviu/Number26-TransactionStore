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

/**
 * Created by liviu on 2/7/2016.
 */
public class TransactionTypeServletTest {

    private static final String BAD_FORMAT_ERROR_JSON = "{\"errors\":\"Bad format error please use 'transactionservice/types/transaction_id'\"}";
    private static final String ID_lONG_JSON = "{\"errors\":\"Id is not a long\"}";

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
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
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

}
