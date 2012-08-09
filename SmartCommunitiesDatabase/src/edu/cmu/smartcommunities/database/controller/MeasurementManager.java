package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.controller.hibernate.HibernateUtil;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.database.model.MeasurementType;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MeasurementManager
   implements Serializable
   {
   private static final String measurementDateTimeColumn = "measurementDateTime";
   private static final long   serialVersionUID          = -6876169729675513328L;

   public Map<Date, Measurement> getMeasurementMap(final Date   beginDateTime,
                                                   final Date   endDateTime,
                                                   final Long   localityId,
                                                   final String name)
      {
      final GetMeasurementsBusinessTransaction businessTransaction = new GetMeasurementsBusinessTransaction(beginDateTime,
                                                                                                            endDateTime,
                                                                                                            localityId,
                                                                                                            name);

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      return businessTransaction.measurementMap;
      }

   public Measurement putMeasurement(final long   localityId,
                                     final Date   measurementDateTime,
                                     final String name,
                                     final double value)
      {
      final PutMeasurementBusinessTransaction businessTransaction = new PutMeasurementBusinessTransaction(localityId,
                                                                                                          measurementDateTime,
                                                                                                          name,
                                                                                                          value);

      HibernateUtil.executeBusinessTransaction(businessTransaction);
      return businessTransaction.measurement;
      }

   private static class GetMeasurementsBusinessTransaction
      implements BusinessTransactionInterface
      {
      private final Date                   beginDateTime;
      private final Date                   endDateTime;
      private final long                   localityId;
      private final Map<Date, Measurement> measurementMap = new HashMap<>();
      private final String                 name;

      public GetMeasurementsBusinessTransaction(final Date   beginDateTime,
                                                final Date   endDateTime,
                                                final long   localityId,
                                                final String name)
         {
         this.beginDateTime = beginDateTime;
         this.endDateTime = endDateTime;
         this.localityId = localityId;
         this.name = name;
         }

      private List<Locality> getSubordinateLocalities(Locality       locality,
                                                      List<Locality> localityList)
         {
         localityList.add(locality);
         for (Locality childLocality:  locality.getChildLocalitySet())
            {
            getSubordinateLocalities(childLocality,
                                     localityList);
            }
         return localityList;
         }

      @Override
      public void execute()
         {
         final Session         session         = HibernateUtil.getSessionFactory().getCurrentSession();
         final MeasurementType measurementType = (MeasurementType) session.createQuery("from MeasurementType as measurementType where measurementType.name = :name")
                                                                          .setString("name", name)
                                                                          .uniqueResult();
         

         if (measurementType != null)
            {
            final Criteria criteria      = session.createCriteria(Measurement.class);
            final boolean  cumulative    = measurementType.isCumulative();
            final Query    localityQuery = session.createQuery("from Locality as locality where locality.id = :localityId")
                                                  .setLong("localityId", localityId);
            final Locality locality      = (Locality) localityQuery.uniqueResult();

            if (cumulative)
               {
               criteria.add(Restrictions.in("locality",
                                            getSubordinateLocalities(locality,
                                                                     new Vector<Locality>())));
               }
            else
               {
               criteria.add(Restrictions.eq("locality",
                                            locality));
               }

            @SuppressWarnings("unchecked")
            final List<Measurement> measurementList = criteria.add(Restrictions.between(measurementDateTimeColumn,
                                                                                        beginDateTime,
                                                                                        endDateTime))
                                                              .add(Restrictions.eq("measurementType",
                                                                                   measurementType))
                                                              .addOrder(Order.asc(measurementDateTimeColumn))
                                                              .list();
         // logger.debug("Measurements found:  " + measurementList.size());

         // final Map<Date, Measurement> measurementMap = new HashMap<>();

            for (Measurement measurement:  measurementList)
               {
               final Date measurementDateTime = measurement.getMeasurementDateTime();

            // logger.debug("measurementDateTime:  " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(measurementDateTime));
               if (cumulative)
                  {
                  final double value = measurement.getValue();

         //       logger.debug("value:  " + value);
                  if (measurementMap.containsKey(measurement.getMeasurementDateTime()))
                     {
                     final Measurement cumulativeMeasurement = measurementMap.get(measurementDateTime);

                     cumulativeMeasurement.setValue(cumulativeMeasurement.getValue() + value);
         //          logger.debug("occupancy:  " + cumulativeMeasurement.getOccupancy() + ", watts:  " + cumulativeMeasurement.getWatts());
                     }
                  else
                     {
                     final Measurement cumulativeMeasurement = new Measurement();

                     cumulativeMeasurement.setValue(value);
                     measurementMap.put(measurementDateTime, cumulativeMeasurement);
         //          logger.debug("occupancy:  " + cumulativeMeasurement.getOccupancy() + ", watts:  " + cumulativeMeasurement.getWatts());
                     }
                  }
               else
                  {
         //       logger.debug("dateTime:  " + simpleDateFormat.format(measurementDateTime) +
         //                    ", carbonDioxide:  " + measurement.getCarbonDioxide() +
         //                    ", humidity:  " + measurement.getHumidity() +
         //                    ", light:  " + measurement.getLight() +
         //                    ", temperature:  " + measurement.getTemperature());
                  measurementMap.put(measurementDateTime, measurement);
                  }
               }
            }
         }
      }

   private static class PutMeasurementBusinessTransaction
      implements BusinessTransactionInterface
      {
      private final long        localityId;
      private       Measurement measurement;
      private final Date        measurementDateTime;
      private final String      name;
      private final double      value;

      public PutMeasurementBusinessTransaction(final long    localityId,
                                               final Date    measurementDateTime,
                                               final String  name,
                                               final double  value)
         {
         this.localityId = localityId;
         this.measurementDateTime = measurementDateTime;
         this.name = name;
         this.value = value;
         }

      @Override
      public void execute()
         {
         final Session         session         = HibernateUtil.getSessionFactory().getCurrentSession();
         final MeasurementType measurementType = (MeasurementType) session.createQuery("from MeasurementType as measurementType where measurementType.name = :name")
                                                                          .setString("name", name).uniqueResult();

         if (measurementType != null)
            {
            final Query             localityQuery   = session.createQuery("from Locality as locality where locality.id = :localityId")
                                                             .setLong("localityId", localityId);
            final Locality          locality        = (Locality) localityQuery.uniqueResult();
            final Criteria          criteria        = session.createCriteria(Measurement.class);
            @SuppressWarnings("unchecked")
            final List<Measurement> measurementList = criteria.add(Restrictions.eq("locality", locality))
                                                              .add(Restrictions.eq("measurementType", measurementType))
                                                              .add(Restrictions.eq(measurementDateTimeColumn, measurementDateTime))
                                                              .list();

            switch (measurementList.size())
               {
               case 0:
                  {
                  measurement = new Measurement();
                  locality.getMeasurementSet().add(measurement);
                  measurement.setLocality(locality);
                  measurement.setMeasurementDateTime(measurementDateTime);
                  measurement.setMeasurementType(measurementType);
                  measurement.setValue(value);
                  session.saveOrUpdate(locality);
                  session.saveOrUpdate(measurement);
                  break;
                  }
               case 1:
                  {
                  measurement = measurementList.get(0);
                  measurement.setValue(value);
                  session.saveOrUpdate(measurement);
                  break;
                  }
               default:
                  {
                  throw new IllegalStateException("Multiple states found for localityId = " + localityId + ", measurementTypeId = " + measurementType.getId() + " and measurementDateTime = " + measurementDateTime);
                  }
               }
            }
         }
      }
   }
