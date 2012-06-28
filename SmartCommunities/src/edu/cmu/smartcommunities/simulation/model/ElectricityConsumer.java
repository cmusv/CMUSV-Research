package edu.cmu.smartcommunities.simulation.model;

/*
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
*/
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class ElectricityConsumer
   implements ElectricityConsumerInterface,
              Serializable
   {
   public /* private */ static final Map<String, ElectricityConsumer>   agentNameMap = new HashMap<String, ElectricityConsumer>();
///private              DBObject                           dbObject     = null;
   public /* private */        final List<ElectricityConsumerInterface> childList    = new ArrayList<ElectricityConsumerInterface>();
   public /* private */ static final String                             collectionName = "Locality";
///private static final DBCollection                       dbCollection;
   public /* private */        final SortedSet<Date>                    dateTimeSet  = new TreeSet<Date>();
   public /* private */        final String                             displayName;
   public /* private */ static final String                             displayNameString = "displayName";
   public /* private */              long                               id;
   public               static       long                               instanceCount = 0;
// private        final Logger                             logger       = LoggerFactory.getLogger(getClass());
   public /* private */              ElectricityConsumerInterface       parent       = null;
   public /* private */ static final String                             parentString = "parent";
   public /* private */        final Map<Date, State>                   stateMap     = new HashMap<Date, State>();
   public /* private */ static final String                             stateString  = "state";
             private           final Double                             watts;

   static
      {
///   dbCollection = Database.getInstance().getCollection(collectionName);
      }

   public ElectricityConsumer(final String agentLocalName,
                              final String displayName)
      {
      this(agentLocalName,
           displayName,
           null);
      }

   public ElectricityConsumer(final String agentLocalName,
                              final String displayName,
                              final Double watts)
      {
      agentNameMap.put(agentLocalName, this);
      this.displayName = displayName;
      id = ++instanceCount;
      this.watts = watts;
///   dbObject = read(displayName);
///   if (dbObject == null)
///      {
///      create(this);
///      }
      }

   @Override
   public void add(ElectricityConsumerInterface electricityConsumer)
      {
      electricityConsumer.setParent(this);
      childList.add(electricityConsumer);
      }
/*
   public static void create(final ElectricityConsumer electricityConsumer)
      {
      electricityConsumer.dbObject = new BasicDBObject();
      electricityConsumer.dbObject.put(displayNameString, electricityConsumer.displayName);
      electricityConsumer.dbObject.put(parentString, null);
      electricityConsumer.dbObject.put(stateString, null);
      dbCollection.insert(electricityConsumer.dbObject);
      }

   public static DBObject read(final String displayName)
      {
      final DBObject query  = new BasicDBObject();

      query.put(displayNameString, displayName);

      final DBCursor cursor = dbCollection.find(query);
            DBObject result = null;

      while (cursor.hasNext())
         {
         result = cursor.next();
         System.out.println("Result is >" + result + "<");
         }
      return result;
      }
*/
   @Override
   public List<ElectricityConsumerInterface> getChildren()
      {
      return childList;
      }

   public static ElectricityConsumer getElectricityConsumer(final String agentLocalName)
      {
      return agentNameMap.get(agentLocalName);
      }

   public /* private */ int getElectricityConsumption(final Date beginDateTime,
                                         final Date endDateTime)
      {
   // logger.trace("Begin getElectricityConsumption");

      int electricityConsumption = 0;

      for (Date dateTime:  dateTimeSet.subSet(beginDateTime, new Date(endDateTime.getTime() + 1)))
         {
         electricityConsumption += stateMap.get(dateTime).electricityConsumption;
         }
   // logger.debug("Returning:  " + electricityConsumption);
   // logger.trace("End   getElectricityConsumption");
      return electricityConsumption;
      }

   @Override
   public Date getMostRecentMeasurementDateTime()
      {
      /*
      Date mostRecentMeasurementDateTime = null;

      System.out.println("Before loop, # keys = " + stateMap.keySet().size());
      for (Date dateTime:  stateMap.keySet())
         {
         System.out.println("Top of loop");
         if ((mostRecentMeasurementDateTime == null) ||
             (dateTime.after(mostRecentMeasurementDateTime)))
            {
            System.out.println("Setting " + dateTime);
            mostRecentMeasurementDateTime = dateTime;
            }
         }
      return mostRecentMeasurementDateTime;
      */
      return dateTimeSet.size() == 0 ? null : dateTimeSet.last();
      }

   @Override
   public int getOccupancy(final Date dateTime)
      {
      return getOccupancy(dateTime,
                          dateTime);
      }

   @Override
   public int getOccupancy(final Date beginDateTime,
                           final Date endDateTime)
      {
   // logger.trace("Begin getOccupancy");
   // logger.debug("Getting occupancy for " + displayName + " between " + beginDateTime + " and " + endDateTime);

      int occupancy = 0;

      synchronized (dateTimeSet)
         {
         for (Date dateTime:  dateTimeSet.subSet(beginDateTime, new Date(endDateTime.getTime() + 1)))
            {
         // occupancy += stateMap.get(dateTime).occupancy;
            occupancy += getState(dateTime).occupancy;
            }
         }
   // logger.debug("Returning " + occupancy);
   // logger.trace("End   getOccupancy");
      return occupancy;
      }

   @Override
   public ElectricityConsumerInterface getParent()
      {
      return parent;
      }

   public /* private */ State getState(final Date dateTime)
      {
   // logger.trace("Begin getState");
   // logger.debug("dateTime:  " + dateTime);

      State state = null;

      synchronized (stateMap)
         {
         state = stateMap.get(dateTime);
         }
      if (state == null)
         {
   //    logger.debug("State not found");
         state = new State();
         synchronized (stateMap)
            {
            stateMap.put(dateTime, state);
            }
      // System.out.println("Putting date for:  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime) + ", # keys = " + stateMap.keySet().size());
         synchronized (dateTimeSet)
            {
            dateTimeSet.add(dateTime);
            }
         }
   // logger.debug("Object reference:  " + state);
   // logger.trace("End   getState");
      return state;
      }

   @Override
   public int getTotalElectricityConsumption(final Date dateTime,
                                             final int  granularity)
      {
      return getTotalElectricityConsumption(dateTime,
                                            new Date(dateTime.getTime() + 3600 * 1000 * (granularity - 1)));
      }

   @Override
   public int getTotalElectricityConsumption(final Date beginDateTime,
                                             final Date endDateTime)
      {
   // logger.trace("Begin getTotalElectricityyConsumption");

      int electricityConsumption = getElectricityConsumption(beginDateTime,
                                                             endDateTime);

      for (ElectricityConsumerInterface electricityConsumer:  childList)
         {
         electricityConsumption += electricityConsumer.getTotalElectricityConsumption(beginDateTime,
                                                                                      endDateTime);
         }
   // logger.debug("Returning:  " + electricityConsumption);
   // logger.trace("End   getTotalElectricityConsumption");
      return electricityConsumption;
      }

   @Override
   public int getTotalOccupancy(final Date dateTime,
                                final int  granularity)
      {
      return getTotalOccupancy(dateTime,
                               new Date(dateTime.getTime() + 3600 * 1000 * (granularity - 1)));
      }

   @Override
   public int getTotalOccupancy(final Date beginDateTime,
                                final Date endDateTime)
      {
   // logger.trace("Begin getTotalOccupancy");

      int occupancy = getOccupancy(beginDateTime,
                                   endDateTime);

      if (occupancy == 0)
         {
         for (ElectricityConsumerInterface electricityConsumer:  childList)
            {
            occupancy += electricityConsumer.getTotalOccupancy(beginDateTime,
                                                               endDateTime);
            }
         }
   // logger.debug("Returning:  " + occupancy);
   // logger.trace("End  getTotalOccupancy");
      return occupancy;
      }

   @Override
   public void initializeState(final Date dateTime)
      {
   // logger.trace("Begin initializeState");

      final int previousOccupancy = dateTimeSet.size() == 0 ? 0 : getOccupancy(dateTimeSet.last());

      getState(dateTime).occupancy = previousOccupancy;
   // logger.trace("End   initializeState");
      }

   @Override
   public void remove(ElectricityConsumerInterface electricityConsumer)
      {
      childList.remove(electricityConsumer);
      }

   @Override
   public void setElectricityConsumption(final int  electricityConsumption,
                                         final Date dateTime)
      {
   // logger.trace("Begin setLocalElectricityConsumption");
   // logger.debug("location:  " + displayName + ", electricity consumption:  " + electricityConsumption + ", dateTime:   " + dateTime);
      getState(dateTime).electricityConsumption = new Double(electricityConsumption);
   // logger.debug("New electricityConsumption:  " + getState(dateTime).electricityConsumption);
   // logger.debug("Object reference:  " + this);
   // logger.trace("End   setLocalElectricityConsumption");
      }

   @Override
   public void setOccupancy(final int  occupancy,
                            final Date dateTime)
      {
   // logger.trace("Begin setOccupancy");
   // logger.debug("location:  " + displayName + ", occupancy:  " + occupancy + ", dateTime:   " + dateTime);
      getState(dateTime).occupancy = occupancy;
   // logger.debug("New occupancy:  " + getState(dateTime).occupancy);
   // logger.debug("Object reference:  " + this);
   // logger.trace("End   setOccupancy");
      setWatts(dateTime);
      }

   @Override
   public void setParent(final ElectricityConsumerInterface electricityConsumer)
      {
      this.parent = electricityConsumer;
      }

   private void setWatts(final Date dateTime)
      {
      if (watts != null)
         {
         final int totalOccupancy = getTotalOccupancy(dateTime,
                                                      new Date(dateTime.getTime() + 1));

      // System.out.println(displayName +
      //                    " @ " +
      //                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateTime) +
      //                    ":  setting watts to " + (totalOccupancy == 0 ? 0 : watts.intValue()));
         setElectricityConsumption(totalOccupancy == 0 ? 0 : watts.intValue(),
                                   dateTime);
         }
      if (parent != null)
         {
         ((ElectricityConsumer) parent).setWatts(dateTime);
         }
      }

   @Override
   public String toString()
      {
      return displayName;
      }

   public String toLongString()
      {
      final String       comma        = ",";
      final DateFormat   dateFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      final StringBuffer stringBuffer = new StringBuffer();

      stringBuffer.append(id)
                  .append(comma)
                  .append(displayName)
                  .append(comma)
                  .append(parent == null ? 0 : ((ElectricityConsumer) parent).id)
                  .append(comma);
      for (Date dateTime:  dateTimeSet)
         {
         final State state = stateMap.get(dateTime);

         stringBuffer.append(dateFormat.format(dateTime))
                     .append(comma)
                     .append(state.electricityConsumption)
                     .append(comma)
                     .append(state.occupancy)
                     .append(comma);
         }
      stringBuffer.append("X\n");
      return stringBuffer.toString();
      }

   public void fromLongString(final String string)
      {
      final DateFormat      dateFormat      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      final StringTokenizer stringTokenizer = new StringTokenizer(string, ",");

      final String          id              = stringTokenizer.nextToken();
      final String          displayName     = stringTokenizer.nextToken();
      final String          parentId        = stringTokenizer.nextToken();

      while (stringTokenizer.countTokens() > 3)
         {
         try
            {
            final Date  dateTime = dateFormat.parse(stringTokenizer.nextToken());
            final State state    = new State();

            dateTimeSet.add(dateTime);
            System.out.println("Adding " +
                               new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime) +
                               " to dateTimeSet, size now " +
                               dateTimeSet.size() +
                               ", last = " +
                               new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getMostRecentMeasurementDateTime()));
            state.electricityConsumption = Double.parseDouble(stringTokenizer.nextToken());
            state.occupancy              = Integer.parseInt(stringTokenizer.nextToken());
            stateMap.put(dateTime, state);
            }
         catch (final ParseException parseException)
            {
            parseException.printStackTrace();
            }
         }
      }

   public class State
      implements Serializable
      {
      public /* private */ Double electricityConsumption = 0D;
      public /* private */ int    occupancy              = 0;
//              private    Double watts                  = 0D;
      }
   }
