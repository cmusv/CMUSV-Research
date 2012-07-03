package edu.cmu.smartcommunities.database.controller.hibernate;

import edu.cmu.smartcommunities.database.controller.BusinessTransactionInterface;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This helper class facilitates working with Hibernate.  It provides the means
 * for:
 * <ul>
 *    <li>obtaining a session factory (and subsequently a session),</li>
 *    <li>defining business transactions, and</li>
 *    <li>wrapping business transactions with a Hibernate transaction.</li>
 * </ul>
 *
 * @author mcsmith
 */

public class HibernateUtil
   {
   private static final Logger         logger         = LoggerFactory.getLogger("smartspaces.database.HibernateUtil");
   private static final SessionFactory sessionFactory;

   static
      {
      try
         {
         sessionFactory = new Configuration().configure().buildSessionFactory();
         }
      catch (Throwable throwable)
         {
         throwable.printStackTrace();
         throw new ExceptionInInitializerError(throwable);
         }
      }

   /**
    * Wraps a business transaction within a Hibernate transaction.
    *
    * @param businessTransaction The class implementing <code>BusinessTransactionInterface</code>
    *                            that contains the business logic.
    *
    * @see   smartspaces.database.controller.hibernate.BusinessTransactionInterface
    */

   public static void executeBusinessTransaction(final BusinessTransactionInterface businessTransaction)
      {
      if (logger.isTraceEnabled())
         {
         logger.trace("Begin HibernateUtil.executeBusinessTransaction");
         }
      try
         {
         Session     session     = null;
         Transaction transaction = null;

         try
            {
            session = getSessionFactory().getCurrentSession();
            logger.info("After getCurrentSession");
            transaction = session.beginTransaction();
            logger.info("After beginTransaction, active?  " + transaction.isActive());
            businessTransaction.execute();
            transaction.commit();
            }
         catch (final Exception exception)
            {
            if ((transaction != null) && (transaction.isActive()))
               {
               transaction.rollback();
               }
            throw exception;
            }
         finally
            {
            if ((session != null) && (session.isOpen()))
               {
               session.close();
               }
            }
         }
      catch (final Throwable throwable)
         {
         logger.error(throwable.getMessage());
         throw new RuntimeException(throwable);
         }
      if (logger.isTraceEnabled())
         {
         logger.trace("End   HibernateUtil.executeBusinessTransaction");
         }
      }

   /**
    * Returns a reference to the SessionFactory singleton.
    * 
    * @return A reference to the SessionFactory singleton.
    */

   public static SessionFactory getSessionFactory()
      {
      return sessionFactory;
      }
   }
