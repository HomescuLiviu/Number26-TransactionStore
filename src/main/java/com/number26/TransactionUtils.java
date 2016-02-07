package com.number26;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liviu on 2/7/2016.
 */
public class TransactionUtils {
    private static final String BAD_FORMAT_ERROR = "Bad format error please use 'transactionservice/transaction/transaction_id'";


    public static Optional<String> validateRequest(HttpServletRequest req, HttpServletResponse resp, List<String> requestValues, String patternString) {

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(req.getPathInfo());

        if (!matcher.matches()) {
            return Optional.of(BAD_FORMAT_ERROR);
        }

        return Optional.empty();
    }
}
