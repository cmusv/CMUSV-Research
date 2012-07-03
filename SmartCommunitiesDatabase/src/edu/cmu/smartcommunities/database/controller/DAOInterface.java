package edu.cmu.smartcommunities.database.controller;

import java.io.Serializable;
import java.util.List;

public interface DAOInterface<GenericType, Identifier extends Serializable>
   {
   GenericType findById(final Identifier identifier,
                        final boolean    lock);

   List<GenericType> findAll();

   List<GenericType> findByExample(final GenericType exampleObject,
                                   final String[]    excludeProperty);

   GenericType makePersistent(final GenericType object);

   void makeTransient(final GenericType object);
   }
