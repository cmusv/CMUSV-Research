package edu.cmu.smartcommunities.jade.core.behaviours;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.util.ExtendedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OneShotBehaviour
   extends jade.core.behaviours.OneShotBehaviour
   {
   protected final ExtendedProperties extendedProperties;
   protected final Logger             logger             = LoggerFactory.getLogger(getClass());

   protected OneShotBehaviour(final Agent              agent,
                              final ExtendedProperties extendedProperties)
      {
      super(agent);
      logger.trace("Begin <init>");
      this.extendedProperties = extendedProperties;
      logger.trace("End   <init>");
      }
   }