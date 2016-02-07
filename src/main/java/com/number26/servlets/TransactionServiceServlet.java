package com.number26.servlets;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.number26.storage.Transaction;
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
public class TransactionServiceServlet extends TransactionServletBase {

    private static final String PARENT_ID_LONG_ERROR = "Parent id {%s} is not a long";
    private static final String AMOUNT_DOUBLE_ERROR = "Amount {%s} is not a double";
    private static final String STATUS_OK = "ok";

    private final TransactionStore transactionStore;

    @Inject
    public TransactionServiceServlet(TransactionStore transactionStore) {
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
            resultJsonBuilder = getTransactionByIdAsJson(resultJsonBuilder, transactionIdString, resp);
            JsonObject resultJson = resultJsonBuilder.build();
            resp.getWriter().append(resultJson.toString());
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();
        resp.setStatus(HttpServletResponse.SC_OK);
        if (isTransactionValid(req, resp)){
            try {
                Transaction transaction = createTransactionFromParameters(req);
                transactionStore.storeTransaction(transaction);
                resultJsonBuilder = resultJsonBuilder.add("status", STATUS_OK);
                resp.getWriter().append(resultJsonBuilder.build().toString());
            } catch (IllegalArgumentException iae){
                setStatusAndErrorMessages(resp, resultJsonBuilder, iae);
            }
        }
    }

    private void setStatusAndErrorMessages(HttpServletResponse resp, JsonObjectBuilder resultJsonBuilder, IllegalArgumentException iae) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resultJsonBuilder = resultJsonBuilder.add("errors", iae.getMessage() );
        JsonObject resultJson = resultJsonBuilder.build();
        resp.getWriter().append(resultJson.toString());
    }

    private Transaction createTransactionFromParameters(HttpServletRequest req) {
        List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());
        String transactionIdString = requestValues.get(requestValues.size() - 1);
        Long id = Long.valueOf(transactionIdString);
        Long parentId =  req.getParameter("parent_id") == null ? null : Long.valueOf(req.getParameter("parent_id"));
        String type = req.getParameter("type");
        Double amount = Double.valueOf(req.getParameter("amount"));
        return new Transaction(id, type, parentId, BigDecimal.valueOf(amount));
    }

    private boolean isTransactionValid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return isIdLong(req, resp) && (isParentIdLong(req, resp)) && isAmountTypeDouble(req, resp);
    }

    private boolean isParentIdLong(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();
        String parentIdString = req.getParameter("parent_id");

        boolean parentIdIsOfTypeLong = true;
        if (parentIdString == null || parentIdString.isEmpty()) return true;
        try {
            Long.valueOf(parentIdString);
        } catch (NumberFormatException iae){
            parentIdIsOfTypeLong = false;
        }
        if (!parentIdIsOfTypeLong){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", String.format(PARENT_ID_LONG_ERROR, parentIdString));
            JsonObject resultJson = resultJsonBuilder.build();
            resp.getWriter().append(resultJson.toString());
        }

        return parentIdIsOfTypeLong;
    }

    private boolean isAmountTypeDouble(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();
        String amountString = req.getParameter("amount");

        boolean amountIsOfTypeDouble = true;
        try {
            Double.valueOf(amountString);
        } catch (NumberFormatException iae){
            amountIsOfTypeDouble = false;
        }
        if (!amountIsOfTypeDouble){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", String.format(AMOUNT_DOUBLE_ERROR, amountString));
            JsonObject resultJson = resultJsonBuilder.build();
            resp.getWriter().append(resultJson.toString());
        }

        return amountIsOfTypeDouble;
    }

    private JsonObjectBuilder getTransactionByIdAsJson(JsonObjectBuilder resultJsonBuilder, String transactionIdString, HttpServletResponse resp) {
        try {
            Transaction result = transactionStore.getTransactionById(Long.valueOf(transactionIdString));
            if (result != null) {
                String parentIdJsonValue = result.getParentId().isPresent() ? String.valueOf(result.getParentId().get()) : "";
                resultJsonBuilder = resultJsonBuilder
                        .add("amount", result.getAmount())
                        .add("type", result.getType())
                        .add("parent_id", parentIdJsonValue);
            }
        } catch (IllegalArgumentException iae){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", iae.getMessage());
        }
        return resultJsonBuilder;
    }
}
