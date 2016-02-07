package com.number26.servlets;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.number26.storage.TransactionStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TransactionTypeServlet extends HttpServlet {

    private final TransactionStore transactionStore;

    public TransactionTypeServlet(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());

        try {
            String transactionType = requestValues.get(requestValues.size() - 1);
            List<String> result = transactionStore.getTransactionsByType(transactionType);
            resp.getWriter().append("[");
            if (result != null && !result.isEmpty()) {
                resp.getWriter().append(Joiner.on(",").join(result));
            }
            resp.getWriter().append("]");
        } catch (IllegalArgumentException iae){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
   }
}
