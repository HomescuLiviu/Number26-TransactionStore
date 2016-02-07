package com.number26.servlets;

import com.google.common.base.Splitter;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.json.Json.createObjectBuilder;

public class TransactionServletBase extends HttpServlet {

    protected static final String ID_lONG_ERROR = "Id {%s} is not a long";

    protected boolean isIdLong(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<String> requestValues = Splitter.on("/").splitToList(req.getPathInfo());
        String transactionIdString = requestValues.get(requestValues.size() - 1);
        JsonObjectBuilder resultJsonBuilder = createObjectBuilder();

        boolean idIsOfTypeLong = true;
        try {
            Long.valueOf(transactionIdString);
        } catch (NumberFormatException iae){
            idIsOfTypeLong = false;
        }
        if (!idIsOfTypeLong){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultJsonBuilder = resultJsonBuilder.add("errors", String.format(ID_lONG_ERROR, transactionIdString));
            JsonObject resultJson = resultJsonBuilder.build();
            resp.getWriter().append(resultJson.toString());
        }
        return idIsOfTypeLong;
    }
}
