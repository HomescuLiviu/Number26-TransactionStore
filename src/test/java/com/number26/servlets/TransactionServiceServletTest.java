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

public class TransactionServiceServletTest {
    private static final String BAD_FORMAT_ERROR_JSON = "{\"errors\":\"Bad format error please use 'transactionservice/transaction/transaction_id'\"}";
    private static final String ID_lONG_JSON = "{\"errors\":\"Id is not a long\"}";

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
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIsEmpty() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIsIncomplete() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }


    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestDoesNotContainAnId() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIsTooLong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/transaction");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestPathStartsWrong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transactionservice/transaction/2");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsNotANumber() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/a");


        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestHasMultipleIds() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2/3");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }


    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsNotALong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/2.3");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(BAD_FORMAT_ERROR_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsErrorCodeAndMessageWhenRequestIdIsTooLong() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/123456789123456789123456789123456789123456789123456789");

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append(ID_lONG_JSON);
    }

    @Test
    public void testGettingTransactionByIdReturnsEmptyJsonWhenStoreIsEmpty() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(transactionStoreMock.getTransactionById(anyLong())).thenReturn(null);
        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(writerMock, times(1)).append("{}");
    }

    @Test
    public void testGettingTransactionByTypeReturnsListOfErrorsWhenStoreRetuensErrors() throws ServletException, IOException {

        when(requestMock.getPathInfo()).thenReturn("/transactionservice/transaction/1");
        when(transactionStoreMock.getTransactionById(anyLong())).thenThrow(new IllegalArgumentException("Test exception"));

        transactionServiceServlet.doGet(requestMock, responseMock);

        verify(responseMock, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock, times(1)).append("{\"errors\":\"Test exception\"}");
    }

}