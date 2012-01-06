package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkSpaceAgent
   extends Agent
   {
   protected              int             capacity;
   private          final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(PersonAgent.physicalLocationOntology);
   protected              Set<String>     occupantSet      = new HashSet<String>();
   private   static final long            serialVersionUID = -7605004311858914283L;

   protected void setup()
      {
      super.setup();
      capacity = extendedProperties.getIntProperty("Capacity", 0);
      addBehaviour(new MonitorOccupancyBehaviour(this));
      }

   private class MonitorOccupancyBehaviour
      extends CyclicBehaviour
      {
      private static final long serialVersionUID = 4790328090611231047L;

      public MonitorOccupancyBehaviour(final Agent agent)
         {
         super(agent);
         logger.trace("Begin MonitorOccupancyBehaviour.<init>");
         logger.trace("End   MonitorOccupancyBehaviour.<init>");
         }

      @Override
      public void action()
         {
         logger.trace("Begin MonitorOccupancyBehaviour.action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            final String content         = inboundMessage.getContent();
            final String senderAgentName = inboundMessage.getSender().getLocalName();

            if (PersonAgent.entering.equals(content))
               {
               logger.debug("Entering:  " + senderAgentName);
               occupantSet.add(senderAgentName);
               }
            else
               {
               if (PersonAgent.leaving.equals(content))
                  {
                  logger.debug("Leaving:  " + senderAgentName);
                  occupantSet.remove(senderAgentName);
                  }
               else
                  {
                  logger.warn("Received unexpected occupancy content:  " + content);
                  }
               }
            if (occupantSet.size() > 0)
               {
               final List<String> occupantList = new ArrayList<String>();

               occupantList.addAll(occupantSet);
               Collections.sort(occupantList);
               for (String occupant:  occupantList)
                  {
                  logger.debug("Occupant:  " + occupant);
                  }
               }
            }
         logger.trace("End   MonitorOccupancyBehaviour.action");
         }
      }
   }
