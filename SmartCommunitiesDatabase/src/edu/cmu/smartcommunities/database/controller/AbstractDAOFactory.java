package edu.cmu.smartcommunities.database.controller;

public abstract class AbstractDAOFactory
   {
   /**
    * Creates a standalone DAOFactory that returns unmanaged DAO beans for use
    * in any environment Hibernate has been configured for. Uses
    * HibernateUtil/SessionFactory and Hibernate context propagation
    * (CurrentSessionContext), thread-bound or transaction-bound, and
    * transaction scoped.
    */

   public static final Class<? extends AbstractDAOFactory> HIBERNATE = edu.cmu.smartcommunities.database.controller.hibernate.DAOFactory.class;

   /**
    * Factory method for instantiation of concrete factories.
    */
   public static AbstractDAOFactory instance(Class<? extends AbstractDAOFactory> daoFactory)
      {
      try
         {
         return daoFactory.newInstance();
         }
      catch (final Exception exception)
         {
         throw new RuntimeException("Couldn't create DAOFactory: " + daoFactory,
                                    exception);
         }
      }

// public abstract CategoryDAOInterface getCategoryDAO();
   }
