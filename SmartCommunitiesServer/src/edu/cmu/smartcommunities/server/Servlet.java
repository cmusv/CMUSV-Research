package edu.cmu.smartcommunities.server;

import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.LocalityManager.LeveledLocality;
import edu.cmu.smartcommunities.database.controller.MeasurementManager;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Servlet
 */

@WebServlet(value="/servlet", loadOnStartup=1)
public class Servlet
   extends HttpServlet
   {
   private static final String               applicationText              = "application/text";
// private static final String               carbonDioxideParameter       = "carbonDioxide";
   private static final String               countParameter               = "count";
// private static final DAOFactory           daoFactory                   = new DAOFactory();
// private static final String               humidityParameter            = "humidity";
   private static final String               leveledLocalitiesParameter   = "leveledLocalities";
// private static final String               lightParameter               = "light";
// private static final String               localOccupancyParameter      = "localOccupancy";
// private static final LocalityDAOInterface localityDAO                  = daoFactory.getLocalityDAO();
   private static final String               localityIdParameter          = "localityId";
   private        final LocalityManager      localityManager              = new LocalityManager();
   private        final Logger               logger                       = LoggerFactory.getLogger(Servlet.class);
   private static final String               measureParameter             = "measure";
   private static final String               measurementDateTimeParameter = "measurementDateTime";
   private        final MeasurementManager   measurementManager           = new MeasurementManager();
   private static final String               measurementsParameter        = "measurements";
// private static final String               occupancyParameter           = "occupancy";
   private static final String               resourceParameter            = "resource";
   private static final long                 serialVersionUID             = -4438267678361119983L;
   private        final DateFormat           simpleDateFormat             = new SimpleDateFormat("yyyyMMddHHmm");
// private static final String               temperatureParameter         = "temperature";
   private        final TimeZone             utcTimeZone                  = TimeZone.getTimeZone("UTC");
// private static final String               wattsParameter               = "watts";

   /**
    * @see HttpServlet#HttpServlet()
    */

   public Servlet()
      {
      super();
      simpleDateFormat.setTimeZone(utcTimeZone);
      }

   /**
    * @see HttpServlet#doGet(HttpServletRequest  request,
    *                        HttpServletResponse response)
    */

   protected void doGet(HttpServletRequest  request,
                        HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin doGet");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());

      final String resource = request.getParameter(resourceParameter);

      if (leveledLocalitiesParameter.equals(resource))
         {
         getLeveledLocalities(request,
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

            final Map<String, String[]> parameterMap = request.getParameterMap();

            logger.warn("Parameters specified:  " + parameterMap.size());
            logger.warn("Begin dumping out parameters");
         // for (String name:  parameterMap.keySet())
            for (Map.Entry<String, String[]> entry:  parameterMap.entrySet())
               {
            // logger.warn("Name:  >" + name + "<");
               logger.warn("Name:  >" + entry.getKey());
            // for (String value:  parameterMap.get(name))
               for (String value:  entry.getValue())
                  {
                  logger.warn("Value:  >" + value + "<");
                  }
               }
            logger.warn("End   dumping out parameters");
            throw new IllegalArgumentException("Unknown request type");
            }
         }
      logger.trace("End   doGet");
      }

   @Override
   public void doPut(HttpServletRequest  request,
                     HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin doPut");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());
      putMeasurement(request,
                     response);
      logger.trace("End   doPut");
      }

   private void getLeveledLocalities(final HttpServletRequest  request,
                                     final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin getLocalities");

      final char   comma   = ',';
      final char   newline = '\n';
      final Writer writer  = response.getWriter();

      response.setContentType(applicationText);
      for (LeveledLocality leveledLocality:  localityManager.getLeveledLocalities())
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
      logger.trace("End   getLeveledLocalities");
      }

   private void getMeasurements(final HttpServletRequest  request,
                                final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin getMeasurements");

      final Map<String, String[]> parameterMap    = request.getParameterMap();
      final Calendar              beginDateTime;
      final int                   count           = Integer.parseInt(parameterMap.get(countParameter)[0]);
      final Calendar              endDateTime     = new GregorianCalendar(utcTimeZone);
      final Long                  localityId      = Long.parseLong(parameterMap.get(localityIdParameter)[0]);
      final String                measurementType = parameterMap.get(measureParameter)[0];

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
      beginDateTime = (Calendar) endDateTime.clone();
      beginDateTime.add(Calendar.MINUTE, -count);

      final char                   delimiter      = ',';
      final StringBuffer           stringBuffer   = new StringBuffer();
      final Writer                 writer         = response.getWriter();
      final Map<Date, Measurement> measurementMap = measurementManager.getMeasurementMap(beginDateTime.getTime(),
                                                                                         endDateTime.getTime(),
                                                                                         localityId,
                                                                                         measurementType);

      response.setContentType(applicationText);
      if (measurementMap.size() == 0)
         {
         for (int i = 0; i < count; i++)
            {
            stringBuffer.append(delimiter);
            }
         }
      else
         {
         beginDateTime.add(Calendar.MINUTE, -1);
         for (int minute = 0; minute < count; minute++)
            {
            beginDateTime.add(Calendar.MINUTE, 1);

            final Date        measurementDateTime = beginDateTime.getTime();
            final Measurement measurement         = measurementMap.get(measurementDateTime);
         // final Object      measurementValue    = measurement == null ? null : measurement.getValue();

            /*
            if (measurement == null)
               {
               measurementValue = null;
               }
            else
               {
               measurementValue = measurement.getValue();
            */
            // switch (measurementType /* measure */)
               /*
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
               */
         // if (measurementValue != null)
            if (measurement != null)
               {
            // stringBuffer.append(measurementValue.toString());
               stringBuffer.append(measurement.getValue());
               }
            stringBuffer.append(delimiter);
            }
         }
      writer.write(stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString());
      response.flushBuffer();
      logger.trace("End   getMeasurements");
      }

   private void putMeasurement(HttpServletRequest  request,
                               HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin putMeasurement");

      try
         {
      //       Double                carbonDioxide       = null;
      //       Double                humidity            = null;
      //       Double                light               = null;
         final Map<String, String[]> parameterMap        = request.getParameterMap();
         final Long                  localityId          = Long.parseLong(parameterMap.get(localityIdParameter)[0]);
         final Date                  measurementDateTime = simpleDateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]);
      //       Integer               occupancy           = null;
      //       Double                temperature         = null;
      //       Double                watts               = null;

      // for (String parameter:  parameterMap.keySet())
         for (Map.Entry<String, String[]> entry:  parameterMap.entrySet())
            {
            final String key = entry.getKey();

         // switch (parameter)
            switch (key)
               {
               case localityIdParameter:
               case measurementDateTimeParameter:
                  {
                  // These have been processed before.
                  break;
                  }
               default:
                  {
                  measurementManager.putMeasurement(localityId,
                                                    measurementDateTime,
                                                 // parameter,
                                                 // Double.valueOf(parameterMap.get(parameter)[0]));
                                                    key,
                                                    Double.valueOf(entry.getValue()[0]));
                  }
               }
            }
         /*
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
         measurementManager.putMeasurement(carbonDioxide,
                                           humidity,
                                           light,
                                           localityId,
                                           measurementDateTime,
                                           occupancy,
                                           temperature,
                                           watts);
                                           */
         }
      catch (final Exception exception)
         {
         logger.error("Error parsing request parameters", exception);
         throw new ServletException(exception);
         }
      logger.trace("End   putMeasurement");
      }
   }
