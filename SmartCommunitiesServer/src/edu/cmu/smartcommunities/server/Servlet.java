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
   private static final String               countParameter               = "count";
   private static final String               leveledLocalitiesParameter   = "leveledLocalities";
   private static final String               localityIdParameter          = "localityId";
   private        final LocalityManager      localityManager              = new LocalityManager();
   private        final Logger               logger                       = LoggerFactory.getLogger(Servlet.class);
   private static final String               measureParameter             = "measure";
   private static final String               measurementDateTimeParameter = "measurementDateTime";
   private        final MeasurementManager   measurementManager           = new MeasurementManager();
   private static final String               measurementTypeParameter     = "measurementType";
   private static final String               measurementTypesParameter    = "measurementTypes";
   private static final String               measurementsParameter        = "measurements";
   private static final String               resourceParameter            = "resource";
   private static final long                 serialVersionUID             = -4438267678361119983L;
   private        final DateFormat           simpleDateFormat             = new SimpleDateFormat("yyyyMMddHHmm");
   private        final TimeZone             utcTimeZone                  = TimeZone.getTimeZone("UTC");

   /**
    * @see HttpServlet#HttpServlet()
    */

   public Servlet()
      {
      super();
      simpleDateFormat.setTimeZone(utcTimeZone);
      }

   /**
    * Requests a representation of the specified resource.  The HTTP GET request should be in the following form:
    * 
    * <code>http://<em>hostname[:port]</em>/SmartCommunitiesServer/servlet?<em>parameters</em>
    * 
    * where:
    * 
    * <dl>
    *    <dt>hostname</dt>
    *    <dd>the name of the server hosting the servlet.</dd>
    *    <dt>port</dt>
    *    <dd>the port number to which the server is listening.</dd>
    *    <dt>parameters</dt>
    *    <dd>an ampersand-concatenated list of the key-value pairs.</dd>
    * </dl>
    * 
    * Only one key value (<code>resource</code>) is required, although other key-value pairs are required or accepted,
    * depending on the value of <code>resource</code>.  The accepted values for <code>resource</code> are:
    * 
    * <dl>
    *    <dt>leveledLocalities</dt>
    *    <dd>returns a list of localities known to the system.</dd>
    *    <dt>measurementTypes</dt>
    *    <dd>returns a list of the measurement types known to the system.</dd>
    *    <dt>measurements</dt>
    *    <dd>returns a list of measurements matching the provided query criteria.</dd>
    * </dl>
    * 
    * <p><strong>Leveled Localities</strong></p>
    * 
    * The system maintains localities in a single-rooted tree structure.  Every locality (other than the root locality) is
    * a child of another locality in the system.  The response to a request for leveled localities is a newline-delimited
    * list of locality entities.  Each locality has a <code>level</code> indicating its generational depth in the tree.  A
    * non-root locality with level <em>n</em> is a child of the most closely preceding locality with a level <em>n-1</em>.
    * Each locality entity is a comma-delimited list of attributes.  The attributes are:
    * 
    * <dl>
    *    <dt>level</dt>
    *    <dd>0 for the root locality, 1 for the root locality's children, 2 for the root locality's grandchildren, etc.</dd>
    *    <dt>localityId</dt>
    *    <dd>the unique identifier for the locality.</dd>
    *    <dt>name</dt>
    *    <dd>the name of the locality.</dd>
    * </dl>
    * 
    * <p>Example request and response:</p>
    *
    * <p><strong>Measurement Types</strong></p>
    *
    * The system maintains a list of the allowed measurement types.  This request provides a newline-delimited list of the
    * measurement types.
    * 
    * <p><strong>Measurements</strong></p>
    * 
    * Additional key-value pairs must be provided to retrieve measurements from the system.
    * 
    * <table>
    *    <thead>
    *       <tr>
    *          <th>Parameter name</th>
    *          <th>Parameter value</th>
    *          <th>Description</th>
    *          <th>Optionality</th>
    *       </tr>
    *    </thead>
    *    <tbody>
    *       <tr>
    *          <td><code>count</code></td>
    *          <td><em>integer</em></td>
    *          <td>The maximum number of measurements to return.</td>
    *          <td>Required</td>
    *       </tr>
    *       <tr>
    *          <td><code>localityId</code></td>
    *          <td><em>integer</em></td>
    *          <td>The unique identifier for the locality to which the measurements apply.</td>
    *          <td>Required</td>
    *       </tr>
    *       <tr>
    *          <td><code>measurementType</code></td>
    *          <td><em>string</em></td>
    *          <td>The type of measurement being requested.</td>
    *          <td>Required</td>
    *       </tr>
    *       <tr>
    *          <td><code>measurementDateTime</code></td>
    *          <td><em>yyyymmddhhmm</em></td>
    *          <td>The date/time, represented in UTC, of the last measurement of interest.</td>
    *          <td>Optional</td>
    *       </tr>
    *    </tbody>
    * </table>
    * 
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

      switch (resource)
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
            for (Map.Entry<String, String[]> entry:  parameterMap.entrySet())
               {
               logger.warn("Name:  >" + entry.getKey() + "<");
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

   /**
    * 
    * @see HttpServlet#doPut(HttpServletRequest  request,
    *                        HttpServletResponse response)
    */

   @Override
   protected void doPut(HttpServletRequest  request,
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

   private void getMeasurementTypes(final HttpServletRequest  request,
                                    final HttpServletResponse response)
      throws ServletException,
             IOException
      {
      logger.trace("Begin getMeasurementTypes");
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
      final String                measure         = parameterMap.get(measureParameter)[0]; // deprecated
      final String                measurementType = measure == null ? parameterMap.get(measurementTypeParameter)[0] : measure;

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
            if (measurement != null)
               {
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
         final Map<String, String[]> parameterMap        = request.getParameterMap();
         final Long                  localityId          = Long.parseLong(parameterMap.get(localityIdParameter)[0]);
         final Date                  measurementDateTime = simpleDateFormat.parse(parameterMap.get(measurementDateTimeParameter)[0]);

         for (Map.Entry<String, String[]> entry:  parameterMap.entrySet())
            {
            final String key = entry.getKey();

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
                                                    key,
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
