package com.number26;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.number26.modules.TransactionServiceModule;

import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        System.out.println("Adding the module");
        return Guice.createInjector(new TransactionServiceModule());
    }
}