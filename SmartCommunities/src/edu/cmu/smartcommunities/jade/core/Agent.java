package edu.cmu.smartcommunities.jade.core;

import jade.util.ExtendedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent
   extends jade.core.Agent
   {
   protected final ExtendedProperties extendedProperties = new ExtendedProperties();
   protected final Logger             logger             = LoggerFactory.getLogger(getClass());

   protected Agent()
      {
      logger.trace("Begin <init>");
      logger.trace("End   <init>");
      }

   public void setExtendedProperties(final String[] arguments)
      {
      logger.trace("Begin setExtendedProperties");
      extendedProperties.addProperties(arguments);
      logger.trace("End   setExtendedProperties");
      }

   protected void setup()
      {
      logger.trace("Begin setup");
      super.setup();

      final Object[] arguments = getArguments();

      if (arguments != null)
         {
         final int    extendedPropertyCount = arguments.length;
         final String extendedProperty[]    = new String[extendedPropertyCount];

         for (int i = extendedPropertyCount - 1; i >= 0; i--)
            {
            extendedProperty[i] = arguments[i].toString();
            }
         setExtendedProperties(extendedProperty);
         }
      logger.trace("End   setup");
      }
   }
