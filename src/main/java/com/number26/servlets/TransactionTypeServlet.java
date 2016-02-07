package com.number26.servlets;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.number26.storage.TransactionStore;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.json.Json.createObjectBuilder;

@Singleton
public class TransactionTypeServlet extends TransactionServletBase {

    private static final String TRANSACTION_TYPE_ERROR = "Error when trying to find transactions with type {%s} : {%s}";
    private final TransactionStore transactionStore;

    @Inject
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
            addErrorMessageInTheResponseJson(resp, requestValues, iae);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
   }

    private void addErrorMessageInTheResponseJson(HttpServletResponse resp, List<String> requestValues, IllegalArgumentException iae) throws IOException {
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();
        resultJsonBuilder = resultJsonBuilder.add("errors", String.format(TRANSACTION_TYPE_ERROR, requestValues.get(requestValues.size() - 1), iae.getMessage()));
        JsonObject resultJson = resultJsonBuilder.build();
        resp.getWriter().append(resultJson.toString());
    }
}
