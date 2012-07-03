package edu.cmu.smartcommunities.database.controller;

/**
 * An interface used for encapsulating business transaction logic that
 * needs to be wrapped by a Hibernate transaction.
 * 
 * @author mcsmith
 */

public interface BusinessTransactionInterface
   {
   /**
    * The method that contains the business transaction logic.
    */

   public void execute();
   }
