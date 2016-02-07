package com.number26.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.number26.storage.TransactionStore;

import javax.servlet.http.HttpServlet;

@Singleton
public class TransactionServiceServlet extends HttpServlet {


    @Inject
    private final TransactionStore transactionStore;

    public TransactionServiceServlet(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }



}
