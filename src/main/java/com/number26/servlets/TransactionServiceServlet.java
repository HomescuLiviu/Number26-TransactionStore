package com.number26.servlets;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.number26.storage.Transaction;
import com.number26.storage.TransactionStore;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.number26.TransactionUtils.validateRequest;

@Singleton
public class TransactionServiceServlet extends HttpServlet {

    private static final String GET_PATH_FORMAT = "[a-z]*/transaction/{1}-?[0-9]+";
    private static final String ID_lONG_ERROR = "Id is not a long";


    private final TransactionStore transactionStore;

    @Inject
    public TransactionServiceServlet(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());
        resp.setContentType("application/json");

        Optional<String> error = validateRequest(req, resp, requestValues, GET_PATH_FORMAT);

        JsonObjectBuilder resultJsonBuilder = Json.createObjectBuilder();

        if (!error.isPresent()) {
            String transactionIdString = requestValues.get(requestValues.size() - 1);
            Double idAsDouble = Double.valueOf(transactionIdString);

            if (idAsDouble > Long.MAX_VALUE || idAsDouble < Long.MIN_VALUE ){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultJsonBuilder = resultJsonBuilder.add("errors", ID_lONG_ERROR);
            } else {
                resultJsonBuilder = getTransactionByIdAsJson(resultJsonBuilder, transactionIdString, resp);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", error.get());
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
