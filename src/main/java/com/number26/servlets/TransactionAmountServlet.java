package com.number26.servlets;

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
import java.math.BigDecimal;
import java.util.List;

import static javax.json.Json.createObjectBuilder;

@Singleton
public class TransactionAmountServlet extends TransactionServletBase {

    private final TransactionStore transactionStore;

    @Inject
    public TransactionAmountServlet(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();

        boolean isIdLong = isIdLong(req, resp);
        if (isIdLong){
            List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());
            String transactionIdString = requestValues.get(requestValues.size() - 1);
            resultJsonBuilder = getTransactionSumAsJson(resultJsonBuilder, transactionIdString, resp);
            JsonObject resultJson = resultJsonBuilder.build();
            resp.getWriter().append(resultJson.toString());
        }
    }

    private JsonObjectBuilder getTransactionSumAsJson(JsonObjectBuilder resultJsonBuilder, String transactionIdString, HttpServletResponse resp) {
        try {
            BigDecimal result = transactionStore.getAmountByTransactionId(Long.valueOf(transactionIdString));
            if (result != null) {
                resultJsonBuilder = resultJsonBuilder
                        .add("sum", result);
            }
        } catch (IllegalArgumentException iae){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", iae.getMessage());
        }
        return resultJsonBuilder;
    }
}
