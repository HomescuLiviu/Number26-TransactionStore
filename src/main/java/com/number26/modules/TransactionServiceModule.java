package com.number26.modules;

import com.google.inject.Binder;
import com.google.inject.servlet.ServletModule;
import com.number26.servlets.TransactionServiceServlet;
import com.number26.servlets.TransactionTypeServlet;

public class TransactionServiceModule extends ServletModule {

    @Override
    protected Binder binder() {
        return super.binder();
    }

    @Override
    protected void configureServlets() {
        serve("/transactionservice/transaction/{1}-?[0-9]+").with(TransactionServiceServlet.class);
        serve("/transactionservice/types/*").with(TransactionTypeServlet.class);
    }
}
