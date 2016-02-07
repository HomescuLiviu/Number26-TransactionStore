package com.number26.servlets;

import com.number26.storage.TransactionStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public class TransactionServiceServletTest {
    private static final String ID_NOT_LONG_ERROR = "{\"errors\":\"Id {%s} is not a long\"}";
    private static final String PARENT_ID_NOT_LONG_ERROR = "{\"errors\":\"Parent id {%s} is not a long\"}";
    private static final String AMOUNT_NOT_DOUBLE_ERROR = "{\"errors\":\"Amount {%s} is not a double\"}";

    private TransactionStore transactionStoreMock = mock(TransactionStore.class);
    private PrintWriter writerMock = mock(PrintWriter.class);
    private HttpServletRequest requestMock = mock(HttpServletRequest.class);
    private HttpServletResponse responseMock = mock(HttpServletResponse.class);
    private TransactionServiceServlet transactionServiceServlet = new TransactionServiceServlet(transactionStoreMock);

    @Before
    public void setup() throws IOException {
        transactionStoreMock = mock(TransactionStore.class);
        transactionServiceServlet = new TransactionServiceServlet(transactionStoreMock);
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);

        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsNotANumber() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/a");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(ID_NOT_LONG_ERROR);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsNotALong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2.3");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(AMOUNT_NOT_DOUBLE_ERROR, 2.3));
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsTooLong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/123456789123456789123456789123456789123456789123456789");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(AMOUNT_NOT_DOUBLE_ERROR, "123456789123456789123456789123456789123456789123456789"));
    }

    @Test
    public void testGettingTransactionByIdReturnsEmptyJsonWhenStoreIsEmpty() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(transactionStoreMock.getTransactionById(anyLong())).thenReturn(null);
        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("{}");
    }

    @Test
    public void testGettingTransactionByIdReturnsListOfErrorsWhenStoreReturnsErrors() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(transactionStoreMock.getTransactionById(anyLong())).thenThrow(new IllegalArgumentException("Test exception"));
        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append("{\"errors\":\"Test exception\"}");
    }

    @Test
    public void testPutTransactionReturnsListOfErrorsWhenStoreReturnsErrors() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        Mockito.doThrow(new IllegalArgumentException("Test exception")).when(transactionStoreMock).storeTransaction(any());

        transactionServiceServlet.doPut(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append("{\"errors\":\"Test exception\"}");
    }

    @Test
    public void testPutTransactionReturnsErrorIfTheParentIdIsNotALong() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(requestMock.getParameter("parent_id")).thenReturn("3.6");
        when(requestMock.getParameter("amount")).thenReturn("2.3");
        when(requestMock.getParameter("type")).thenReturn("some");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(PARENT_ID_NOT_LONG_ERROR, "3.6"));
    }

    @Test
    public void testPutTransactionReturnsErrorIfTheParentIdIsNotANumber() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(requestMock.getParameter("parent_id")).thenReturn("acb");
        when(requestMock.getParameter("amount")).thenReturn("2.3");
        when(requestMock.getParameter("type")).thenReturn("some");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(PARENT_ID_NOT_LONG_ERROR, "acb"));
    }

    @Test
    public void testPutTransactionReturnsErrorIfTheParentIdIsNotADouble() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(requestMock.getParameter("parent_id")).thenReturn("2");
        when(requestMock.getParameter("amount")).thenReturn("erty");
        when(requestMock.getParameter("type")).thenReturn("some");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(String.format(AMOUNT_NOT_DOUBLE_ERROR, "erty"));
    }


    @Test
    public void testCanPutOneTransaction() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");

        when(requestMock.getParameter("parent_id")).thenReturn(null);
        when(requestMock.getParameter("amount")).thenReturn("2.3");
        when(requestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(writerMock, never()).append(any());
        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_OK);

        localTransactionServiceServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("{\"amount\":2.3,\"type\":\"someType\",\"parent_id\":\"\"}");
    }


    @Test
    public void testCanPutMultipleTransactios() throws ServletException, IOException {
        TransactionStore transactionStore = new TransactionStore();
        TransactionServiceServlet localTransactionServiceServlet = new TransactionServiceServlet(transactionStore);
        HttpServletRequest secondRequestMock = mock(HttpServletRequest.class);

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");

        when(requestMock.getParameter("parent_id")).thenReturn(null);
        when(requestMock.getParameter("amount")).thenReturn("2.3");
        when(requestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(requestMock, responseMock);

        verify(responseMock, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(writerMock, never()).append(any());

        when(secondRequestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2");
        when(secondRequestMock.getParameter("parent_id")).thenReturn("1");
        when(secondRequestMock.getParameter("amount")).thenReturn("2.3");
        when(secondRequestMock.getParameter("type")).thenReturn("someType");

        localTransactionServiceServlet.doPut(secondRequestMock, responseMock);

        verify(writerMock, never()).append(any());
        verify(responseMock, times(2)).setStatus(HttpServletResponse.SC_OK);

        localTransactionServiceServlet.doGet(requestMock, responseMock);
        localTransactionServiceServlet.doGet(secondRequestMock, responseMock);

        verify(writerMock, times(1)).append("{\"amount\":2.3,\"type\":\"someType\",\"parent_id\":\"\"}");
        verify(writerMock, times(1)).append("{\"amount\":2.3,\"type\":\"someType\",\"parent_id\":\"1\"}");
    }

}