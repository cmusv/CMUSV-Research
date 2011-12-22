package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.OneShotBehaviour;
import edu.cmu.smartcommunities.services.BroadcastMessageBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassTimeBehaviour
   extends OneShotBehaviour
   {
   private        final long             clockTickInterval;
   private        final DateFormat       dateFormat               = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private        final int              millisecondsPerClockTick;
   private        final long             startTime;
   private        final long             stopTime;
   private        final Map<String, AID> subscriberMap;
   private              Thread           timeSimulatorThread      = new Thread(new TimeSimulator(), "TimeSimulator");

   protected PassTimeBehaviour(final Agent              agent,
                               final ExtendedProperties extendedProperties,
                               final Map<String, AID>   subscriberMap)
      {
      super(agent,
            extendedProperties); // .extractSubset(agent.getLocalName() + ".PassTimeBehaviour."));

      clockTickInterval = getPropertyValue("ClockTickInterval", 1000);
      millisecondsPerClockTick = getPropertyValue("MillisecondsPerClockTick", 1000);
      startTime = getDatePropertyValue("StartTime", "2012-01-01 00:00:00");
      stopTime = getDatePropertyValue("StopTime", "2012-12-31 23:59:59");
      this.subscriberMap = subscriberMap;
      }

   public void action()
      {
      timeSimulatorThread.start();
      }

   private long getDatePropertyValue(final String propertyName,
                                     final String defaultValue)
      {
      long propertyValue = 0;
      
      try
         {
         propertyValue = dateFormat.parse(extendedProperties.getProperty(propertyName)).getTime();
         }
      catch (final Exception exception)
         {
         logger.warn("Unable to interpret " + propertyName,
                     exception);
         }
      return propertyValue;
      }

   private int getPropertyValue(final String propertyName,
                                final int    defaultValue)
      {
      int propertyValue = 0;

      try
         {
         propertyValue = extendedProperties.getIntProperty(propertyName, defaultValue);
         }
      catch (final NumberFormatException numberFormatException)
         {
         logger.warn("Unable to interpret " + propertyName,
                     numberFormatException);
         }
      return propertyValue;
      }

   private class TimeSimulator
      implements Runnable
      {
      final Logger logger = LoggerFactory.getLogger(getClass());

      public void run()
         {
         logger.trace("Begin run");

         boolean done          = false;
         long    simulatedTime = startTime;

         while (!done)
            {
            /*
            myAgent.addBehaviour(new BroadcastMessageBehaviour((Agent) myAgent,
                                                               "(time " + simulatedTime + ")",
                                                               TimeAgent.TIME_SIMULATION_ONTOLOGY,
                                                               ACLMessage.INFORM,
                                                               subscriberMap));
            */
            try
               {
               Thread.sleep(clockTickInterval);
               done = (simulatedTime += millisecondsPerClockTick) >= stopTime;
               }
            catch (final InterruptedException interruptedException)
               {
               done = true;
               }
            }
         logger.trace("End   run");
         }
      }
   }
