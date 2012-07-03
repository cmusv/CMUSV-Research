package edu.cmu.smartcommunities.database.controller.hibernate;

import org.hibernate.Session;
import edu.cmu.smartcommunities.database.controller.AbstractDAOFactory;
import edu.cmu.smartcommunities.database.controller.LocalityDAOInterface;
import edu.cmu.smartcommunities.database.controller.MeasurementDAOInterface;

public class DAOFactory
   extends AbstractDAOFactory
   {
   public LocalityDAOInterface getLocalityDAO()
      {
      return (LocalityDAOInterface) instantiateDAO(LocalityDAO.class);
      }

   public MeasurementDAOInterface getStateDAO()
      {
      return (MeasurementDAOInterface) instantiateDAO(MeasurementDAO.class);
      }

   private AbstractDAO<?, ?> instantiateDAO(Class<? extends AbstractDAO<?, ?>> daoClass)
      {
      try
         {
         final AbstractDAO<?, ?> dao = daoClass.newInstance();

         dao.setSession(getCurrentSession());
         return dao;
         }
      catch (final Exception exception)
         {
         throw new RuntimeException("Can not instantiate DAO:  " + daoClass,
                                    exception);
         }
      }

   // You could override this if you don't want HibernateUtil for lookup
   public Session getCurrentSession()
      {
      return HibernateUtil.getSessionFactory().getCurrentSession();
      }
   }
