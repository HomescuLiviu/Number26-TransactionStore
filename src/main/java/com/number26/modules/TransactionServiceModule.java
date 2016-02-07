package com.number26.modules;

import com.google.inject.Binder;
import com.google.inject.servlet.ServletModule;
import com.number26.servlets.TransactionAmountServlet;
import com.number26.servlets.TransactionServiceServlet;
import com.number26.servlets.TransactionTypeServlet;

public class TransactionServiceModule extends ServletModule {

    @Override
    protected Binder binder() {
        return super.binder();
    }

    @Override
    protected void configureServlets() {
        System.out.print("Setting up the servlets");
        serve("/transactionservice/transaction/{1}-?[0-9]+").with(TransactionServiceServlet.class);
        serve("/transactionservice/types/*").with(TransactionTypeServlet.class);
        serve("/transactionservice/sum/{1}-?[0-9]+").with(TransactionAmountServlet.class);

    }
}
