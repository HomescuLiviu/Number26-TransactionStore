package com.number26.servlets;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.number26.storage.Transaction;
import com.number26.storage.TransactionStore;
import org.apache.commons.lang.math.NumberUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class TransactionServiceServlet extends HttpServlet {

    private static final String NOT_NUMBER_ERROR = "Id {%s} is not a number";
    private static final String ID_lONG_ERROR = "Id {%s} is not a long";

    private final TransactionStore transactionStore;

    @Inject
    public TransactionServiceServlet(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());
        resp.setContentType("application/json");

        JsonObjectBuilder resultJsonBuilder = Json.createObjectBuilder();

        String transactionIdString = requestValues.get(requestValues.size() - 1);
        boolean idIsALong = true;
        try {
            Long.valueOf(transactionIdString);
        } catch (IllegalArgumentException iae){
            idIsALong = false;
        }

        boolean idIsANumber = NumberUtils.isNumber(transactionIdString);
        if ( idIsANumber && idIsALong){
            resultJsonBuilder = getTransactionByIdAsJson(resultJsonBuilder, transactionIdString, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", idIsANumber ? String.format(ID_lONG_ERROR, transactionIdString) : String.format(NOT_NUMBER_ERROR, transactionIdString));
        }

        JsonObject resultJson = resultJsonBuilder.build();
        resp.getWriter().append(resultJson.toString());

    }

    private JsonObjectBuilder getTransactionByIdAsJson(JsonObjectBuilder resultJsonBuilder, String transactionIdString, HttpServletResponse resp) {
        try {
            Transaction result = transactionStore.getTransactionById(Long.valueOf(transactionIdString));
            if (result != null) {
                resultJsonBuilder = resultJsonBuilder
                        .add("amount", result.getAmount())
                        .add("type", result.getType())
                        .add("parent_id", result.getParentId().get());
            }
        } catch (IllegalArgumentException iae){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", iae.getMessage());
        }
        return resultJsonBuilder;
    }


}
