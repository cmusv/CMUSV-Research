package edu.cmu.smartcommunities.jade.core.behaviours;

import edu.cmu.smartcommunities.jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CyclicBehaviour
   extends jade.core.behaviours.CyclicBehaviour
   {
   protected final Logger logger = LoggerFactory.getLogger(getClass());

   protected CyclicBehaviour(final Agent agent)
      {
      super(agent);
      logger.trace("Begin <init>");
      logger.trace("End   <init>");
      }
   }
