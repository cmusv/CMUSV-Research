package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.controller.hibernate.HibernateUtil;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.MeasurementType;
import edu.cmu.smartcommunities.database.model.Sensor;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalityManager
   implements Serializable
   {
   private        final Logger logger           = LoggerFactory.getLogger(LocalityManager.class);
   private static final long   serialVersionUID = 9154096251552344796L;

   public Locality getById(final long id)
      {
      logger.trace("Begin getById");

      final GetByIdBusinessTransaction businessTransaction = new GetByIdBusinessTransaction(id);

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      logger.trace("End   getById");
      return businessTransaction.locality;
      }

   public List<LeveledLocality> getLeveledLocalities()
      {
      logger.trace("Begin getLeveledLocalities");

      final GetLeveledLocalitiesBusinessTransaction businessTransaction = new GetLeveledLocalitiesBusinessTransaction();

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      logger.trace("End   getLeveledLocalities");
      return businessTransaction.leveledLocalityList;
      }

   public Sensor getSensor(final long  id,
                           final String name)
      {
      logger.trace("Begin getSensor");

      final GetSensorBusinessTransaction businessTransaction = new GetSensorBusinessTransaction(id,
                                                                                                name);

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      logger.trace("End   getSensor");
      return businessTransaction.sensor;
      }

   public static void main(final String[] argument)
      {
      final LocalityManager localityManager = new LocalityManager();

      for (final String name:  argument)
         {
         final Sensor sensor = localityManager.getSensor(6, name);
         System.out.println("The sensorId for the " + name + " sensor in localityId 6 is :  " + sensor.getId());
         }
      }

   private static class GetByIdBusinessTransaction
      implements BusinessTransactionInterface
      {
      private final long     id;
      private       Locality locality = null;

      public GetByIdBusinessTransaction(final long id)
         {
         this.id = id;
         }

      @Override
      public void execute()
         {
         locality = (Locality) HibernateUtil.getSessionFactory().getCurrentSession().get(Locality.class, id);
         }
      }

   private class GetLeveledLocalitiesBusinessTransaction
      implements BusinessTransactionInterface
      {
      private        final List<LeveledLocality> leveledLocalityList           = new Vector<>();
      private static final String                parentLocalityAssociationPath = "parentLocality";

      private void appendToList(final int      level,
                                final Locality locality)
         {
         logger.trace("Begin GetLeveledLocalitiesBusinessTransaction.appendToList");
         leveledLocalityList.add(new LeveledLocality(level,
                                                     locality));
         for (final Locality childLocality:  locality.getChildLocalitySet())
            {
            appendToList(level + 1,
                         childLocality);
            }
         logger.trace("End   GetLeveledLocalitiesBusinessTransaction.appendToList");
         }

      @Override
      public void execute()
         {
         logger.trace("Begin GetLeveledLocalitiesBusinessTransaction.execute");

      // final Query          localityQuery = HibernateUtil.getSessionFactory().getCurrentSession().createQuery("from Locality as locality where locality.parentLocality is null");
         @SuppressWarnings("unchecked")
      // final List<Locality> localityList  = localityQuery.list();
         final List<Locality> localityList = HibernateUtil.getSessionFactory()
                                                          .getCurrentSession()
                                                          .createCriteria(Locality.class)
                                                          .add(Restrictions.isNull(parentLocalityAssociationPath))
                                                          .list();

         for (final Locality locality:  localityList)
            {
            appendToList(0,
                         locality);
            }
         logger.trace("End   GetLeveledLocalitiesBusinessTransaction.execute");
         }
      }

   private static class GetSensorBusinessTransaction
      implements BusinessTransactionInterface
      {
      private        final long     id;
      private static final String   localityAssociationPath               = "locality";
      private static final String   measurementTypePropertyName           = "measurementType";
      private        final String   name;
      private static final String   namePropertyName                      = "name";
      private              Sensor   sensor                                = null;
      private static final String   sensorPlatformAssociationPath         = "sensorPlatform";
      private static final String   sensorPlatformLocationAssociationPath = "sensorPlatformLocationSet";

      private GetSensorBusinessTransaction(final long   id,
                                           final String name)
         {
         this.id = id;
         this.name = name;
         }

      @Override
      public void execute()
         {
         final Session         session         = HibernateUtil.getSessionFactory()
                                                              .getCurrentSession();
         final MeasurementType measurementType = (MeasurementType) session.createCriteria(MeasurementType.class)
                                                                          .add(Restrictions.eq(namePropertyName, name))
                                                                          .uniqueResult();

         if (measurementType != null)
            {
            @SuppressWarnings("unchecked")
            final List<Sensor> sensorList = session.createCriteria(Sensor.class)
                                                   .add(Restrictions.eq(measurementTypePropertyName, measurementType))
                                                   .createCriteria(sensorPlatformAssociationPath)
                                                   .createCriteria(sensorPlatformLocationAssociationPath)
                                                   .add(Restrictions.isNull("endDateTime"))
                                                   .createCriteria(localityAssociationPath)
                                                   .add(Restrictions.idEq(id))
                                                   .list();

            if (sensorList.size() != 0)
               {
               sensor = sensorList.get(0);
               }
            }
         }
      }

   public static class LeveledLocality
      {
      public final int      level;
      public final Locality locality;

      public LeveledLocality(final int      level,
                             final Locality locality)
         {
         this.level    = level;
         this.locality = locality;
         }
      }
   }
