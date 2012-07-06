package edu.cmu.smartcommunities.server;

import edu.cmu.smartcommunities.database.controller.hibernate.HibernateUtil;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
// import edu.cmu.smartcommunities.database.controller.LocalityDAOInterface;
// import edu.cmu.smartcommunities.database.controller.hibernate.DAOFactory;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Servlet
 */

@WebServlet(value="/servlet", loadOnStartup=1)
public class Servlet
   extends HttpServlet
   {
   private static final String               applicationText               = "application/text";
   private static final String               carbonDioxideParameter        = "carbonDioxide";
   private static final String               countParameter                = "count";
// private static final DAOFactory           daoFactory                    = new DAOFactory();
// private static final String               dateFormat                    = "yyyyMMddHHmm";
   private static final String               humidityParameter             = "humidity";
   private static final String               lightParameter                = "light";
   private static final String               localOccupancyParameter       = "localOccupancy";
   private static final String               localitiesParameter           = "localities";
// private static final LocalityDAOInterface localityDAO                   = daoFactory.getLocalityDAO();
// private              long                 localityId                    = 1;
   private static final String               localityIdParameter           = "localityId";
   private        final Map<Long, Locality>  localityMap                   = new HashMap<>();
// private static final String               locallyConsumedWattsParameter = "locallyConsumedWatts";
   private        final Logger               logger                        = LoggerFactory.getLogger(Servlet.class);
   private static final String               measureParameter              = "measure";
   private static final String               measurementDateTimeColumn     = "measurementDateTime";
   private static final String               measurementDateTimeParameter  = "measurementDateTime";
   private static final String               measurementsParameter         = "measurements";
   private static final String               occupancyParameter            = "occupancy";
   private static final String               resourceParameter             = "resource";
   private static final long                 serialVersionUID              = -4438267678361119983L;
   private        final DateFormat           simpleDateFormat              = new SimpleDateFormat("yyyyMMddHHmm");
   private static final String               temperatureParameter          = "temperature";
   private        final TimeZone             utcTimeZone                   = TimeZone.getTimeZone("UTC");
   private static final String               wattsParameter                = "watts";

   /**
    * @see HttpServlet#HttpServlet()
    */

   public Servlet()
      {
      super();
      simpleDateFormat.setTimeZone(utcTimeZone);
      }

   private void appendToList(final int                   level,
                             final List<LeveledLocality> leveledLocalityList,
                             final Locality              locality)
      {
      localityMap.put(locality.getId(), locality);
      leveledLocalityList.add(new LeveledLocality(level,
                                                  locality));
      for (Locality childLocality:  locality.getChildLocalitySet())
         {
         appendToList(level + 1,
                      leveledLocalityList,
                      childLocality);
         }
      }

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
    *      response)
    */

   protected void doGet(HttpServletRequest  request,
                        HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.debug("Begin doGet");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());
      System.out.println("URL:  " + request.getRequestURL() + request.getQueryString());

      final String resource = request.getParameter(resourceParameter);

      if (localitiesParameter.equals(resource))
         {
         getLocalities(request,
                       response);
         }
      else
         {
         if (measurementsParameter.equals(resource))
            {
            getMeasurements(request,
                            response);
            }
         else
            {
            logger.warn("Invalid parameter specification");
            java.util.Map<String, String[]> parameterMap = request.getParameterMap();

            logger.warn("Parameters specified:  " + parameterMap.size());
            logger.warn("Begin dumping out parameters");
            for (String name:  parameterMap.keySet())
               {
               logger.warn("Name:  >" + name + "<");
               for (String value:  parameterMap.get(name))
                  {
                  logger.warn("Value:  >" + value + "<");
                  }
               }
            logger.warn("End   dumping out parameters");
            throw new IllegalArgumentException("Unknown request type");
            }
         }
      logger.debug("End   doGet");
      }

   @Override
   public void doPut(HttpServletRequest  request,
                     HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.debug("Begin doPut");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());
      System.out.println("URL:  " + request.getRequestURL() + request.getQueryString());
      putMeasurement(request,
                     response);
      logger.debug("End   doPut");
      }

   private void getLocalities(final HttpServletRequest  request,
                              final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.debug("Begin getLocalities");

      final Session     session     = HibernateUtil.getSessionFactory().getCurrentSession();
      final Transaction transaction = session.beginTransaction();

      try
         {
         final List<LeveledLocality> leveledLocalityList = new Vector<>();
         final Query                 localityQuery       = session.createQuery("from Locality as locality where locality.parentLocality is null");
         @SuppressWarnings("unchecked")
         final List<Locality>        localityList        = localityQuery.list();

         for (Locality locality:  localityList)
            {
            appendToList(0,
                         leveledLocalityList,
                         locality);
            }
         transaction.commit();

         final char   comma   = ',';
         final char   newline = '\n';
         final Writer writer  = response.getWriter();

         response.setContentType(applicationText);
         for (LeveledLocality leveledLocality:  leveledLocalityList)
            {
            final int          level        = leveledLocality.level;
            final Locality     locality     = leveledLocality.locality;
            final StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(level)
                        .append(comma)
                        .append(locality.getId())
                        .append(comma)
                        .append(locality.getName())
                        .append(newline);
            writer.write(stringBuffer.toString()); 
            }
         response.flushBuffer();
         }
      catch (final RuntimeException runtimeException)
         {
         if (transaction.isActive())
            {
            transaction.rollback();
            }
         runtimeException.printStackTrace();
         }
      finally
         {
         if (session.isOpen())
            {
            session.close();
            }
         }
      System.out.println("End   getLocalities");
      }

   private void getMeasurements(final HttpServletRequest  request,
                                final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      final Session     session     = HibernateUtil.getSessionFactory().getCurrentSession();
      final Transaction transaction = session.beginTransaction();

      try
         {
         final Map<String, String[]> parameterMap            = request.getParameterMap();
         final int                   count                   = Integer.parseInt(parameterMap.get(countParameter)[0]);
         final Criteria              criteria                = session.createCriteria(Measurement.class);
         final String                measure                 = parameterMap.get(measureParameter)[0];
         final boolean               aggregatingMeasurements = occupancyParameter.equals(measure) ||
                                                               wattsParameter.equals(measure);
         final Long                  localityId              = Long.parseLong(parameterMap.get(localityIdParameter)[0]);
         final Query                 localityQuery           = session.createQuery("from Locality as locality where locality.id = " + localityId); // TODO:  eliminate SQL injection attack vector
         final Locality              locality                = (Locality) localityQuery.uniqueResult();

         if (aggregatingMeasurements)
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

         final Calendar endDateTime = new GregorianCalendar(utcTimeZone);

         try
            {
            endDateTime.setTime(simpleDateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]));
            }
         catch (final Exception exception)
            {
            endDateTime.setTime(new Date());
            }
         endDateTime.set(Calendar.SECOND, 0);
         endDateTime.set(Calendar.MILLISECOND, 0);

         final Calendar beginDateTime = (Calendar) endDateTime.clone();

         beginDateTime.add(Calendar.MINUTE, -count);
      // logger.debug("Querying date/time criteria:  " + simpleDateFormat.format(beginDateTime.getTime()) + " and " +
      //                                                 simpleDateFormat.format(endDateTime.getTime()));
      // final DateFormat debugDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
         
         @SuppressWarnings("unchecked")
         final List<Measurement> measurementList = criteria.add(Restrictions.between(measurementDateTimeColumn,
                                                                                     beginDateTime.getTime(),
                                                                                     endDateTime.getTime()))
                                                           .addOrder(Order.asc(measurementDateTimeColumn))
                                                           .list();

         transaction.commit();
      // logger.debug("Measurements found:  " + measurementList.size());

         final Map<Date, Measurement> measurementMap = new HashMap<>();

         for (Measurement measurement:  measurementList)
            {
            final Date measurementDateTime = measurement.getMeasurementDateTime();

         // logger.debug("measurementDateTime:  " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(measurementDateTime));
            if (aggregatingMeasurements)
               {
               final Integer occupancy = measurement.getOccupancy();
               final Double  watts     = measurement.getWatts();

      //       logger.debug("occupancy:  " + occupancy + ", watts:  " + watts);
               if (measurementMap.containsKey(measurement.getMeasurementDateTime()))
                  {
                  final Measurement cumulativeMeasurement = measurementMap.get(measurementDateTime);

                  if (occupancy != null)
                     {
                     Integer cumulativeOccupancy = cumulativeMeasurement.getOccupancy();

                     if (cumulativeOccupancy == null)
                        {
                        cumulativeOccupancy = occupancy;
                        }
                     else
                        {
                        cumulativeOccupancy += occupancy;
                        }
                     cumulativeMeasurement.setOccupancy(cumulativeOccupancy);
                     }
                  if (watts != null)
                     {
                     Double cumulativeWatts = cumulativeMeasurement.getWatts();

                     if (cumulativeWatts == null)
                        {
                        cumulativeWatts = watts;
                        }
                     else
                        {
                        cumulativeWatts += watts;
                        }
                     cumulativeMeasurement.setWatts(cumulativeWatts);
                     }
      //          logger.debug("occupancy:  " + cumulativeMeasurement.getOccupancy() + ", watts:  " + cumulativeMeasurement.getWatts());
                  }
               else
                  {
                  final Measurement cumulativeMeasurement = new Measurement();

                  cumulativeMeasurement.setOccupancy(occupancy);
                  cumulativeMeasurement.setWatts(watts);
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

      // logger.debug("Measurements stored:  " + measurementMap.size());
      // Date elusiveDateTime = null;
      // for (Date dateTime:  measurementMap.keySet())
      //    {
      //    logger.debug("Date of stored measurement:  " + simpleDateFormat.format(dateTime) + "(" + dateTime.getTime() + ")");
      //    elusiveDateTime = dateTime;
      //    }
      // logger.debug("Time zone for beginDateTime:  " + beginDateTime.getTimeZone().getDisplayName());
         final char         delimiter    = ',';
         final StringBuffer stringBuffer = new StringBuffer();
         final Writer       writer       = response.getWriter();

         response.setContentType(applicationText);
         if (measurementMap.size() == 0)
            {
         // final String noMeasurements = "0 0\n";

         // System.out.println("most recent measurement date time is null");
            for (int i = 0; i < count; i++)
               {
               stringBuffer.append(delimiter);
               }
            }
         else
            {
         // logger.debug("Measurements:  " + measurementMap.size());
         // for (Date dateTime:  measurementMap.keySet())
         //    {
         //    logger.debug("Key:  " + debugDateFormat.format(dateTime));
         //    }
         // final char newline = '\n';
         // final char space   = ' ';

            beginDateTime.add(Calendar.MINUTE, -1);
            for (int minute = 0; minute < count; minute++)
               {
               beginDateTime.add(Calendar.MINUTE, 1);

               final Date        measurementDateTime = beginDateTime.getTime();
            // logger.debug("Getting data for:  " + simpleDateFormat.format(measurementDateTime) + " (" + measurementDateTime.getTime() + ")");
               final Measurement measurement         = measurementMap.get(measurementDateTime);
               final Object      measurementValue;

            // if (beginDateTime.getTime().equals(elusiveDateTime))
            //    {
            //    logger.debug("Looking at elusive datetime value");
            //    }
               if (measurement == null)
                  {
                  measurementValue = null;
               // logger.debug("no data");
                  }
               else
                  {
               // logger.debug("dateTime:  " + simpleDateFormat.format(measurementDateTime) +
               //              ", carbonDioxide:  " + measurement.getCarbonDioxide() +
               //              ", humidity:  " + measurement.getHumidity() +
               //              ", light:  " + measurement.getLight() +
               //              ", temperature:  " + measurement.getTemperature());
                  switch (measure)
                     {
                     case carbonDioxideParameter:
                        {
                        measurementValue = measurement.getCarbonDioxide();
                        break;
                        }
                     case humidityParameter:
                        {
                        measurementValue = measurement.getHumidity();
                        break;
                        }
                     case lightParameter:
                        {
                        measurementValue = measurement.getLight();
                        break;
                        }
                     case occupancyParameter:
                        {
                        measurementValue = measurement.getOccupancy();
                        break;
                        }
                     case temperatureParameter:
                        {
                        measurementValue = measurement.getTemperature();
                        break;
                        }
                     case wattsParameter:
                        {
                        measurementValue = measurement.getWatts();
                        break;
                        }
                     default:
                        {
                        measurementValue = null;
                        }
                     }
                  }
               if (measurementValue != null)
                  {
                  stringBuffer.append(measurementValue.toString());
                  }
               stringBuffer.append(delimiter);
               }
            }
         writer.write(stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString());
         response.flushBuffer();
         }
      catch (final RuntimeException runtimeException)
         {
         if (transaction.isActive())
            {
            transaction.rollback();
            }
         runtimeException.printStackTrace();
         }
      finally
         {
         if (session.isOpen())
            {
            session.close();
            }
         }
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

   private void putMeasurement(HttpServletRequest  request,
                               HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.debug("Begin putState");

      try
         {
               Double                carbonDioxide       = null;
      // final DateFormat            dateFormat          = new SimpleDateFormat(Servlet.dateFormat);
               Double                humidity            = null;
               Double                light               = null;
         final Map<String, String[]> parameterMap        = request.getParameterMap();
         final Long                  localityId          = Long.parseLong(parameterMap.get(localityIdParameter)[0]);
         final Date                  measurementDateTime = simpleDateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]);
               Integer               occupancy           = null;
               Double                temperature         = null;
               Double                watts               = null;

         try
            {
            carbonDioxide = new Double(parameterMap.get(carbonDioxideParameter)[0]);
            }
         catch (final Exception exception)
            {
            }
         try
            {
            humidity = new Double(parameterMap.get(humidityParameter)[0]);
            }
         catch (final Exception exception)
            {
            }
         try
            {
            light = new Double(parameterMap.get(lightParameter)[0]);
            }
         catch (final Exception exception)
            {
            }
         try
            {
            occupancy = new Integer(parameterMap.get(localOccupancyParameter)[0]);
            }
         catch (final Exception exception)
            {
            try
               {
               occupancy = new Integer(parameterMap.get(occupancyParameter)[0]);
               }
            catch (final Exception nestedException)
               {
               }
            }
         try
            {
            temperature = new Double(parameterMap.get(temperatureParameter)[0]);
            }
         catch (final Exception exception)
            {
            }
         try
            {
            watts = new Double(parameterMap.get(wattsParameter)[0]);
            }
         catch (final Exception exception)
            {
            }

         final Session     session     = HibernateUtil.getSessionFactory().getCurrentSession();
         final Transaction transaction = session.beginTransaction();

         try
            {
            final Query                 localityQuery   = session.createQuery("from Locality as locality where locality.id = " + localityId); // TODO:  eliminate SQL injection attack vector
            final Locality              locality        = (Locality) localityQuery.uniqueResult();
         // final Query                 stateQuery      = session.createQuery("from State as state where localityId = " + localityId + " and measurementDateTime = ");
            final Criteria              criteria        = session.createCriteria(Measurement.class);
            @SuppressWarnings("unchecked")
            final List<Measurement>     measurementList = criteria.add(Restrictions.eq("locality", locality))
                                                                  .add(Restrictions.eq(measurementDateTimeColumn, measurementDateTime))
                                                                  .list();

            switch (measurementList.size())
               {
               case 0:
                  {
                  final Measurement measurement = new Measurement();

                  locality.getMeasurementSet().add(measurement);
                  if (carbonDioxide != null)
                     {
                     measurement.setCarbonDioxide(carbonDioxide);
                     }
                  if (humidity != null)
                     {
                     measurement.setHumidity(humidity);
                     }
                  if (light != null)
                     {
                     measurement.setLight(light);
                     }
                  measurement.setLocality(locality);
                  measurement.setMeasurementDateTime(measurementDateTime);
                  if (occupancy != null)
                     {
                     measurement.setOccupancy(occupancy);
                     }
                  if (temperature != null)
                     {
                     measurement.setTemperature(temperature);
                     }
                  if (watts != null)
                     {
                     measurement.setWatts(watts);
                     }
                  session.saveOrUpdate(locality);
                  session.saveOrUpdate(measurement);
                  break;
                  }
               case 1:
                  {
                  final Measurement measurement = measurementList.get(0);

                  if (carbonDioxide != null)
                     {
                     measurement.setCarbonDioxide(carbonDioxide);
                     }
                  if (humidity != null)
                     {
                     measurement.setHumidity(humidity);
                     }
                  if (light != null)
                     {
                     measurement.setLight(light);
                     }
                  if (occupancy != null)
                     {
                     measurement.setOccupancy(occupancy);
                     }
                  if (temperature != null)
                     {
                     measurement.setTemperature(temperature);
                     }
                  if (watts != null)
                     {
                     measurement.setWatts(watts);
                     }
                  session.saveOrUpdate(measurement);
                  break;
                  }
               default:
                  {
                  throw new IllegalStateException("Multiple states found for localityId = " + localityId + " and measurementDateTime = " + measurementDateTime);
                  }
               }
            transaction.commit();
            }
         catch (final Exception exception)
            {
            if (transaction.isActive())
               {
               transaction.rollback();
               }
            logger.error("Error", exception);
            throw exception;
            }
         finally
            {
            if (session.isOpen())
               {
               session.close();
               }
            }
         }
      catch (final Exception exception)
         {
         logger.error("Error parsing request parameters", exception);
         throw new ServletException(exception);
         }
      logger.debug("End   putState");
      }

   private static class LeveledLocality
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
