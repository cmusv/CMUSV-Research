package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.controller.hibernate.HibernateUtil;
import edu.cmu.smartcommunities.database.model.Locality;
import java.util.List;
import java.util.Vector;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalityManager
   {
   private final Logger logger = LoggerFactory.getLogger(LocalityManager.class);

   public List<LeveledLocality> getLeveledLocalities()
      {
      logger.trace("Begin getLeveledLocalities");

      final GetLeveledLocalitiesBusinessTransaction businessTransaction = new GetLeveledLocalitiesBusinessTransaction();

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      logger.trace("End   getLeveledLocalities");
      return businessTransaction.leveledLocalityList;
      }

   private class GetLeveledLocalitiesBusinessTransaction
      implements BusinessTransactionInterface
      {
      private final List<LeveledLocality> leveledLocalityList = new Vector<>();

      private void appendToList(final int      level,
                                final Locality locality)
         {
         logger.trace("Begin GetLeveledLocalitiesBusinessTransaction.appendToList");
         leveledLocalityList.add(new LeveledLocality(level,
                                                     locality));
         for (Locality childLocality:  locality.getChildLocalitySet())
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

         final Query          localityQuery = HibernateUtil.getSessionFactory().getCurrentSession().createQuery("from Locality as locality where locality.parentLocality is null");
         @SuppressWarnings("unchecked")
         final List<Locality> localityList  = localityQuery.list();

         for (Locality locality:  localityList)
            {
            appendToList(0,
                         locality);
            }
         logger.trace("End   GetLeveledLocalitiesBusinessTransaction.execute");
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
