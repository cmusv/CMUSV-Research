package edu.cmu.smartcommunities.server;

import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.LocalityManager.LeveledLocality;
import edu.cmu.smartcommunities.database.controller.MeasurementManager;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.database.model.MeasurementType;
import java.io.IOException;
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
 * This servlet is the gateway for putting resources (data) into and getting resources from the system's database via HTTP PUT and GET
 * requests.
 */

@WebServlet(value="/servlet", loadOnStartup=1)
public class Servlet
   extends HttpServlet
   {
   private static final String             applicationText              = "application/text";
   private static final String             countParameter               = "count";
   private        final DateFormat         dateFormat                   = new SimpleDateFormat("yyyyMMddHHmm");
   private static final String             leveledLocalitiesParameter   = "leveledLocalities";
   private static final String             localityIdParameter          = "localityId";
   private        final LocalityManager    localityManager              = new LocalityManager();
   private        final Logger             logger                       = LoggerFactory.getLogger(Servlet.class);
   @Deprecated
   private static final String             measureParameter             = "measure";
   private static final String             measurementDateTimeParameter = "measurementDateTime";
   private        final MeasurementManager measurementManager           = new MeasurementManager();
   private static final String             measurementParameter         = "measurement";
   private static final String             measurementTypeParameter     = "measurementType";
   private static final String             measurementTypesParameter    = "measurementTypes";
   private static final String             measurementsParameter        = "measurements";
   private static final String             resourceParameter            = "resource";
   private static final long               serialVersionUID             = -8272585844088908248L;
   private        final TimeZone           utcTimeZone                  = TimeZone.getTimeZone("UTC");

   /**
    * @see HttpServlet#HttpServlet()
    */

   public Servlet()
      {
      super();
      dateFormat.setTimeZone(utcTimeZone);
      }

   /**
    * <p>Requests a representation of the specified resource.  The HTTP GET request should be in the following form:</p>
    * 
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunitiesServer/servlet?<em>parameters</em></code></p>
    * 
    * <p>where:
    *    <dl>
    *       <dt>hostname</dt>
    *       <dd>the name of the server hosting the servlet.</dd>
    *       <dt>port</dt>
    *       <dd>the port number to which the server is listening.</dd>
    *       <dt>parameters</dt>
    *       <dd>an ampersand-concatenated list of the key-value pairs.</dd>
    *    </dl>
    * </p>
    *
    * <h5>Resources</h5>
    *
    * <p>Only one key value (<code>resource</code>) is required, although other key-value pairs are required or accepted,
    * depending on the value of <code>resource</code>.  The accepted values for <code>resource</code> are:
    *    <dl>
    *       <dt>leveledLocalities</dt>
    *       <dd>returns a list of localities known to the system.</dd>
    *       <dt>measurementTypes</dt>
    *       <dd>returns a list of the measurement types known to the system.</dd>
    *       <dt>measurements</dt>
    *       <dd>returns a list of measurements matching the provided query criteria.</dd>
    *    </dl>
    * </p>
    *
    * <h6>Leveled Localities</h6>
    * 
    * <p>The system maintains localities in a single-rooted tree structure.  Every locality (other than the root locality) is
    * a child of another locality in the system.  The response to a request for leveled localities is a newline-delimited
    * list of locality entities.  Each locality has a <code>level</code> indicating its generational depth in the tree.  A
    * non-root locality with level <em>n</em> is a child of the most closely preceding locality with a level <em>n-1</em>.
    * Each locality entity is a comma-delimited list of attributes.  The attributes are:
    *    <dl>
    *       <dt>level</dt>
    *       <dd>0 for the root locality, 1 for the root locality's children, 2 for the root locality's grandchildren, etc.</dd>
    *       <dt>localityId</dt>
    *       <dd>the unique identifier for the locality.</dd>
    *       <dt>name</dt>
    *       <dd>the name of the locality.</dd>
    *    </dl>
    * </p>
    * 
    * <p>For example, to get a list of the leveled localities issue the following request:</p>
    *
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunities/servlet?resource=leveledLocalities</code></p>
    * 
    * <p>A fragment of the response may look something like:
    *    <pre>
    *       0,1,NASA Ames Research Center
    *       1,71,Building 19
    *       2,72,Corridor 103x
    *       2,73,Corridor 105x
    *    </pre>
    * </p>
    *
    * <h6>Measurement Types</h6>
    *
    * <p>The system maintains a list of the recognized measurement types.  The response to a request for measurement types is a
    * newline-delimited list of the names of the measurement types.</p>
    *
    * <p>For example, to get a list of measurement types issue the following request:</p>
    * 
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunities/servlet?resource=measurementTypes</code></p>
    * 
    * <p>A fragment of the response may look something like:
    *    <pre>
    *       carbonDioxide
    *       humidity
    *       light
    *       occupancy
    *    </pre>
    * </p>
    * 
    * <h6>Measurements</h6>
    *
    * <p>The system maintains the measurements gathered by the deployed sensors.  A request for measurements must include criteria
    * for defining a subset of the system's measurements.  The criteria include the following:
    *    <dl>
    *       <dt>count</dt>
    *       <dd>An integer representing the maximum number of measurements to return.  (Required)</dd>
    *       <dt>localityId</dt>
    *       <dd>An integer identifying the locality to which the measurements apply.  (Required)</dd>
    *       <dt>measurementType</dt>
    *       <dd>A string representing the name of the measurement type being requested.  (Required)</dd>
    *       <dt>measurementDateTime</dt>
    *       <dd>The date and time, represented in UTC, of the last desired measurement.  (Optional, formatted as YYYYMMDDHHMM, defaults to current time)</dd>
    *    </dl>
    * </p>
    *
    * <p>The measurement values are returned in a comma-delimited string.  Consecutive commas indicate the absence of a measurement
    * for the corresponding time period.</p>
    * 
    * <p>For example, to get a list of measurements issue the following request:</p>
    * 
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunities/servlet?resource=measurements&localityId=6&count=10&measurementType=occupancy</code></p>
    *
    * <p>The response may look something like:
    *    <pre>
    *       1.0,1.0,1.0,1.0,2.0,2.0,2.0,,,1.0
    *    </pre>
    * </p>
    *
    * @see HttpServlet#doGet(HttpServletRequest  request,
    *                        HttpServletResponse response)
    */

   protected void doGet(final HttpServletRequest  request,
                        final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin doGet");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());

      switch (request.getParameter(resourceParameter))
         {
         case leveledLocalitiesParameter:
            {
            getLeveledLocalities(request,
                                 response);
            break;
            }
         case measurementTypesParameter:
            {
            getMeasurementTypes(request,
                                response);
            break;
            }
         case measurementsParameter:
            {
            getMeasurements(request,
                            response);
            break;
            }
         default:
            {
            logger.warn("Invalid parameter specification");

            final Map<String, String[]> parameterMap = request.getParameterMap();

            logger.warn("Parameters specified:  " + parameterMap.size());
            logger.warn("Begin dumping out parameters");
            for (final Map.Entry<String, String[]> entry:  parameterMap.entrySet())
               {
               logger.warn("Name:  >" + entry.getKey() + "<");
               for (final String value:  entry.getValue())
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

   /**
    * <p>Uploads a representation of the specified resource.  The HTTP PUT request should be in the following form:</p>
    * 
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunitiesServer/servlet?<em>parameters</em></code></p>
    * 
    * <p>where:
    *    <dl>
    *       <dt>hostname</dt>
    *       <dd>the name of the server hosting the servlet.</dd>
    *       <dt>port</dt>
    *       <dd>the port number to which the server is listening.</dd>
    *       <dt>parameters</dt>
    *       <dd>an ampersand-concatenated list of the key-value pairs.</dd>
    *    </dl>
    * </p>
    *
    * <h5>Resources</h5>
    *
    * <p>Only one key value (<code>resource</code>) is required, although other key-value pairs are required or accepted,
    * depending on the value of <code>resource</code>.  Currently the one accepted value for <code>resource</code> is:
    *    <dl>
    *       <dt>measurements</dt>
    *       <dd>A set of measurements to be added to the system.</dd>
    *    </dl>
    * </p>
    * 
    * <h6>Measurements</h6>
    *
    * <p>The additional parameters that are recognized when putting measurements into the system are:
    *    <dl>
    *       <dt>measurementDateTime</dt>
    *       <dd>The date and time, represented in UTC, representing when the measurement was taken.  (Required, formatted as YYYYMMDDHHMM)</dd>
    *       <dt><em>sensorId</em></dt>
    *       <dd>The identifiers for the sensor providing the measurement, which must be representable as a floating point number</dd>
    *    </dl>
    * </p>
    * 
    * <p>For example, assuming that the sensor with sensorId of 5 measures temperature and the sensor with sensorId 6 measures humidity and that
    * measurements of 22C with 50% relative humidity were taken at noon (PDT) on June 1, 2012, the following request should be submitted:</p>
    * 
    * <p><code>http://<em>hostname[:port]</em>/SmartCommunities/servlet?resource=measurements&measurementDateTime=201206011900&5=22&6=0.5</code></p>
    *
    * <p><strong>Note:</strong> Remember to represent the time in UTC.</p>
    * 
    * @see HttpServlet#doPut(HttpServletRequest  request,
    *                        HttpServletResponse response)
    */

   @Override
   protected void doPut(final HttpServletRequest  request,
                        final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin doPut");
      logger.debug("URL:  " + request.getRequestURL() + "?" + request.getQueryString());

      switch (request.getParameter(resourceParameter))
         {
         case measurementParameter:
         default:
            {
            putMeasurement(request,
                           response);
            }
         }
      logger.trace("End   doPut");
      }

   private void getLeveledLocalities(final HttpServletRequest  request,
                                     final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin getLocalities");

      final char         fieldDelimiter = ',';
      final char         lineDelimiter  = '\n';
      final StringBuffer stringBuffer   = new StringBuffer();

      for (final LeveledLocality leveledLocality:  localityManager.getLeveledLocalities())
         {
         final int      level    = leveledLocality.level;
         final Locality locality = leveledLocality.locality;
 
         stringBuffer.append(level)
                     .append(fieldDelimiter)
                     .append(locality.getId())
                     .append(fieldDelimiter)
                     .append(locality.getName())
                     .append(lineDelimiter);
         }
      response.setContentType(applicationText);
      response.getWriter().write(stringBuffer.toString()); 
      response.flushBuffer();
      logger.trace("End   getLeveledLocalities");
      }

   private void getMeasurementTypes(final HttpServletRequest  request,
                                    final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin getMeasurementTypes");

      final char         lineDelimiter = '\n';
      final StringBuffer stringBuffer  = new StringBuffer();

      for (final MeasurementType measurementType:  new MeasurementManager().getMeasurementTypeList())
         {
         stringBuffer.append(measurementType.getName())
                     .append(lineDelimiter);
         }
      response.setContentType(applicationText);
      response.getWriter().write(stringBuffer.toString());
      response.flushBuffer();
      logger.trace("End   getMeasurementTypes");
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
      final String[]              measure         = parameterMap.get(measureParameter);
      final String                measurementType = measure == null ? parameterMap.get(measurementTypeParameter)[0] : measure[0];

      try
         {
         endDateTime.setTime(dateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]));
         }
      catch (final Exception exception)
         {
         endDateTime.setTime(new Date());
         }
      endDateTime.set(Calendar.SECOND, 0);
      endDateTime.set(Calendar.MILLISECOND, 0);
      beginDateTime = (Calendar) endDateTime.clone();
      beginDateTime.add(Calendar.MINUTE, -count);

      final char                   fieldDelimiter = ',';
      final Map<Date, Measurement> measurementMap = measurementManager.getMeasurementMap(beginDateTime.getTime(),
                                                                                         endDateTime.getTime(),
                                                                                         localityId,
                                                                                         measurementType);
      final StringBuffer           stringBuffer   = new StringBuffer();

      if (measurementMap.size() == 0)
         {
         for (int i = 0; i < count; i++)
            {
            stringBuffer.append(fieldDelimiter);
            }
         }
      else
         {
         beginDateTime.add(Calendar.MINUTE, -1);
         for (int minute = 0; minute < count; minute++)
            {
            beginDateTime.add(Calendar.MINUTE, 1);

            final Measurement measurement = measurementMap.get(beginDateTime.getTime());

            if (measurement != null)
               {
               stringBuffer.append(measurement.getValue());
               }
            stringBuffer.append(fieldDelimiter);
            }
         }
      response.setContentType(applicationText);
      response.getWriter().write(stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString());
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
         final Map<String, String[]> parameterMap        = request.getParameterMap();
         final Date                  measurementDateTime = dateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]);

         for (final Map.Entry<String, String[]> entry:  parameterMap.entrySet())
            {
            final String key = entry.getKey();

            switch (key)
               {
               case resourceParameter:
               case measurementDateTimeParameter:
                  {
                  // These has been processed already.
                  break;
                  }
               default:
                  {
                  measurementManager.putMeasurement(measurementDateTime,
                                                    Long.valueOf(key),  // This is the sensorId
                                                    Double.valueOf(entry.getValue()[0]));
                  }
               }
            }
         }
      catch (final Exception exception)
         {
         logger.error("Error parsing request parameters", exception);
         throw new ServletException(exception);
         }
      logger.trace("End   putMeasurement");
      }
   }
