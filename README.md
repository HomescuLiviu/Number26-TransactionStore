# Number26-TransactionStore

Getting started 

Git clone git@github.com:HomescuLiviu/Number26-TransactionStore.git from the command line

Or

https://github.com/HomescuLiviu/Number26-TransactionStore.git form an IDE

This application is packaged using maven. 

Implementation @ design description :

1. Transaction.java is the object which stores a transaction. Ih has the 4 basic pieces from data we get when creating a trnasaction 
 and a fith one called "totalAmount". 
 
  "totalAmount" is the added value of all the transactions related to the one it belongs to. Updating the total amount is done by 
  a new thread so that the adding of a transaction can be quick.
  
2. TransactionStore.java is a database that stores all the transactions and returns them by id or type.
    Is stores data in a ConcurrentHashMap so data access can be thread safe.
   TransactionStoreTest has a test called "testStoreCorrectlyReturnsAmountForTransactionIdWhenRequestsAreParallel" which simulates
    100 concurrent threads adding transactions to the sore.
    
There are 3 Servlets:

1. TransactionAmountServlet which is mapped to "/transactionservice/sum/{1}-?[0-9]+" which retuns the sum of all amounts for a transaction given the transacion_id
2. TransactionTypeServlet which is mapped to ""/transactionservice/types/*"" which returns a list if transaction ids that match the transaction type
3. TransactionAmountServlet which is mapped to "/transactionservice/sum/{1}-?[0-9]+" which is responsible for adding trnasactions via http PUT 
                                                                                    and returning a transaction given the transacion_id 
    
The application creats a ".war" filw which can be deployed to a web container(Tomcat, Jboss, Jetty...etc)

The application has been deployed in 2 ways :

1. Run a local tomcat on posrt 8083 with password and user admin(port password and user are configurable in pom.xml)

2. Run on Jetty using the command in jetty-startup-command.txt file

  
  
  


