package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.controller.hibernate.HibernateUtil;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.database.model.MeasurementType;
import edu.cmu.smartcommunities.database.model.Sensor;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MeasurementManager
   implements Serializable
   {
   private static final String measurementDateTimeAttributeName = "measurementDateTime";
   private static final String nameAttributeName                = "name";
   private static final String sensorEntityName                 = "sensor";
   private static final long   serialVersionUID                 = -6876169729675513328L;

   public Map<Date, Measurement> getMeasurementMap(final Date   beginDateTime,
                                                   final Date   endDateTime,
                                                   final Long   localityId,
                                                   final String name)
      {
      final GetMeasurementMapBusinessTransaction businessTransaction = new GetMeasurementMapBusinessTransaction(beginDateTime,
                                                                                                                endDateTime,
                                                                                                                localityId,
                                                                                                                name);

      synchronized (Measurement.class)
         {
         HibernateUtil.executeBusinessTransaction(businessTransaction);
         }
      return businessTransaction.measurementMap;
      }

   public List<MeasurementType> getMeasurementTypeList()
      {
      final GetMeasurementTypeListBusinessTransaction businessTransaction = new GetMeasurementTypeListBusinessTransaction();

      synchronized (Measurement.class)
         {
         HibernateUtil.executeBusinessTransaction(businessTransaction);
         }
      return businessTransaction.measurementTypeList;
      }

   public Measurement putMeasurement(final Date   measurementDateTime,
                                     final long   sensorId,
                                     final double value)
      {
      final PutMeasurementBusinessTransaction businessTransaction = new PutMeasurementBusinessTransaction(measurementDateTime,
                                                                                                          sensorId,
                                                                                                          value);

      synchronized (Measurement.class)
         {
         HibernateUtil.executeBusinessTransaction(businessTransaction);
         }
      return businessTransaction.measurement;
      }

   public void putMeasurements(final List<Measurement> measurementList,
                               final long              sensorId)
      {
      final PutMeasurementsBusinessTransaction businessTransaction = new PutMeasurementsBusinessTransaction(measurementList,
                                                                                                            sensorId);

      synchronized (Measurement.class)
         {
         HibernateUtil.executeBusinessTransaction(businessTransaction);
         }
      }

   public static void main(final String[] argument)
      throws Exception
      {
      final java.text.DateFormat   dateFormat         = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
      final Date                   beginDateTime      = dateFormat.parse("2012-07-31 13:00");
      final Date                   endDateTime        = dateFormat.parse("2012-08-02 13:00");
      final long                   localityId         = 6;
      final MeasurementManager     measurementManager = new MeasurementManager();
      final Map<Date, Measurement> measurementMap     = measurementManager.getMeasurementMap(beginDateTime,
                                                                                             endDateTime,
                                                                                             localityId,
                                                                                             "simulatedOccupancy");

      System.out.println("Measurements:  " + measurementMap.size());
      for (Map.Entry<Date, Measurement> entry:  measurementMap.entrySet())
         {
         final Measurement measurement = entry.getValue();

         System.out.println(dateFormat.format(measurement.getMeasurementDateTime()) + ":  " + measurement.getValue());
         }

      final Date   measurementDateTime = dateFormat.parse("2012-08-01 00:00");
      final long   sensorId            = 16;
      final double value               = 0;

      measurementManager.putMeasurement(measurementDateTime,
                                        sensorId,
                                        value);

      final List<Measurement> measurementList = new Vector<>();
      final Calendar          dateTime        = Calendar.getInstance();

      dateTime.set(Calendar.SECOND, 0);
      dateTime.set(Calendar.MILLISECOND, 0);
      for (int minute = 0; minute < 60; minute++)
         {
         final Measurement measurement = new Measurement();

         dateTime.set(Calendar.MINUTE, minute);
         measurement.setMeasurementDateTime(new Date(dateTime.getTime().getTime()));
         measurement.setValue(minute);
         measurementList.add(measurement);
         }
      measurementManager.putMeasurements(measurementList,
                                         sensorId);
      }

   private static class GetMeasurementMapBusinessTransaction
      implements BusinessTransactionInterface
      {
      private        final Date                   beginDateTime;
      private static final String                 beginDateTimeAttributeName            = "beginDateTime";
      private        final Date                   endDateTime;
      private static final String                 endDateTimeAttributeName              = "endDateTime";
      private static final String                 localityEntityName                    = "locality";
      private        final long                   localityId;
      private static final String                 localityIdAttributeName               = "id";
      private static final String                 measurementEntityName                 = "measurement";
      private static final String                 measurementDateTimeAliasName          = measurementEntityName + "." + measurementDateTimeAttributeName;
      private        final Map<Date, Measurement> measurementMap                        = new TreeMap<>();
      private static final String                 measurementTypeEntityName             = "measurementType";
      private        final String                 name;
      private static final String                 sensorPlatformEntityName              = "sensorPlatform";
      private static final String                 sensorPlatformLocationAssociationName = "sensorPlatformLocationSet";

      public GetMeasurementMapBusinessTransaction(final Date   beginDateTime,
                                                  final Date   endDateTime,
                                                  final long   localityId,
                                                  final String name)
         {
         this.beginDateTime = beginDateTime;
         this.endDateTime = endDateTime;
         this.localityId = localityId;
         this.name = name;
         }

      private List<Locality> getSubordinateLocalities(final Locality       locality,
                                                      final List<Locality> localityList)
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
         final MeasurementType measurementType = (MeasurementType) session.createCriteria(MeasurementType.class)
                                                                          .add(Restrictions.eq(nameAttributeName, name))
                                                                          .uniqueResult();

         if (measurementType != null)
            {
            final Locality locality = (Locality) session.createCriteria(Locality.class)
                                                        .add(Restrictions.eq(localityIdAttributeName, localityId))
                                                        .uniqueResult();

            if (locality != null)
               {
               final boolean           cumulative      = measurementType.isCumulative();
               @SuppressWarnings("unchecked")
               final List<Measurement> measurementList = session.createCriteria(Measurement.class,
                                                                                measurementEntityName)
                                                                .add(Restrictions.between(measurementDateTimeAttributeName,
                                                                                          beginDateTime,
                                                                                          endDateTime))
                                                                .addOrder(Order.asc(measurementDateTimeAttributeName))
                                                                .createCriteria(sensorEntityName)
                                                                .add(Restrictions.eq(measurementTypeEntityName,
                                                                                     measurementType))
                                                                .createCriteria(sensorPlatformEntityName)
                                                                .createCriteria(sensorPlatformLocationAssociationName)
                                                                .add(Restrictions.and(Restrictions.le(beginDateTimeAttributeName,
                                                                                                      beginDateTime),
                                                                                      Restrictions.leProperty(beginDateTimeAttributeName,
                                                                                                              measurementDateTimeAliasName),
                                                                                      Restrictions.or(Restrictions.isNull(endDateTimeAttributeName),
                                                                                                      Restrictions.and(Restrictions.ge(endDateTimeAttributeName,
                                                                                                                                       endDateTime),
                                                                                                                       Restrictions.geProperty(endDateTimeAttributeName,
                                                                                                                                               measurementDateTimeAliasName))),
                                                                                      cumulative ? Restrictions.in(localityEntityName,
                                                                                                                   getSubordinateLocalities(locality,
                                                                                                                                            new Vector<Locality>())) :
                                                                                                   Restrictions.eq(localityEntityName,
                                                                                                                   locality)))
                                                                .list();

               for (final Measurement measurement:  measurementList)
                  {
                  final Date measurementDateTime = measurement.getMeasurementDateTime();

                  if (cumulative)
                     {
                     final Measurement cumulativeMeasurement;
                     final double      value = measurement.getValue();

                     if (measurementMap.containsKey(measurement.getMeasurementDateTime()))
                        {
                        cumulativeMeasurement = measurementMap.get(measurementDateTime);
                        cumulativeMeasurement.setValue(cumulativeMeasurement.getValue() + value);
                        }
                     else
                        {
                        cumulativeMeasurement = new Measurement();
                        cumulativeMeasurement.setMeasurementDateTime(measurementDateTime);
                        cumulativeMeasurement.setSensor(measurement.getSensor());
                        cumulativeMeasurement.setValue(value);
                        measurementMap.put(measurementDateTime, cumulativeMeasurement);
                        }
                     }
                  else
                     {
                     measurementMap.put(measurementDateTime, measurement);
                     }
                  }
               }
            }
         }
      }

   private static class GetMeasurementTypeListBusinessTransaction
      implements BusinessTransactionInterface
      {
      private List<MeasurementType> measurementTypeList = null;

      @Override
      @SuppressWarnings("unchecked")
      public void execute()
         {
         measurementTypeList = HibernateUtil.getSessionFactory()
                                            .getCurrentSession()
                                            .createCriteria(MeasurementType.class)
                                            .addOrder(Order.asc(nameAttributeName))
                                            .list();
         }
      }

   private static class PutMeasurementBusinessTransaction
      implements BusinessTransactionInterface
      {
      private       Measurement measurement         = null;
      private final Date        measurementDateTime;
      private final long        sensorId;
      private final double      value;

      public PutMeasurementBusinessTransaction(final Date    measurementDateTime,
                                               final long    sensorId,
                                               final double  value)
         {
         this.measurementDateTime = measurementDateTime;
         this.sensorId = sensorId;
         this.value = value;
         }

      @Override
      public void execute()
         {
         final Session session = HibernateUtil.getSessionFactory()
                                              .getCurrentSession();

         measurement = (Measurement) session.createCriteria(Measurement.class)
                                            .add(Restrictions.eq(measurementDateTimeAttributeName,
                                                                 measurementDateTime))
                                            .createCriteria(sensorEntityName)
                                            .add(Restrictions.idEq(sensorId))
                                            .uniqueResult();
         if (measurement == null)
            {
            final Sensor sensor = (Sensor) session.createCriteria(Sensor.class)
                                                  .add(Restrictions.idEq(sensorId))
                                                  .uniqueResult();

            if (sensor != null)
               {
               measurement = new Measurement();
               measurement.setMeasurementDateTime(measurementDateTime);
               measurement.setSensor(sensor);
               measurement.setValue(value);
               sensor.getMeasurementSet().add(measurement);
               session.saveOrUpdate(measurement);
               session.saveOrUpdate(sensor);
               }
            }
         else
            {
            measurement.setValue(value);
            session.saveOrUpdate(measurement);
            }
         }
      }

   private static class PutMeasurementsBusinessTransaction
      implements BusinessTransactionInterface
      {
      final List<Measurement> measurementList;
      final long              sensorId;

      public PutMeasurementsBusinessTransaction(final List<Measurement> measurementList,
                                                final long              sensorId)
         {
         this.measurementList = measurementList;
         this.sensorId = sensorId;
         }

      @Override
      public void execute()
         {
         final Session session = HibernateUtil.getSessionFactory()
                                              .getCurrentSession();
         final Sensor  sensor  = (Sensor) session.get(Sensor.class, sensorId);

         if (sensor != null)
            {
            final Set<Measurement> measurementSet = sensor.getMeasurementSet();

            for (final Measurement measurement:  measurementList)
               {
               measurement.setSensor(sensor);
               if (measurementSet.contains(measurement))
                  {
                  measurementSet.remove(measurement);
                  }
               measurementSet.add(measurement);
               }
            session.saveOrUpdate(sensor);
            }
         }
      }
   }
