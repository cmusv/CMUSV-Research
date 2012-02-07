package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.utilities.Parser;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkSpaceAgent
   extends Agent
   {
   protected              int             capacity;
   private   static final String          className            = WorkSpaceAgent.class.getName();
   protected        final AID             coolingZoneAgentId   = new AID();
// protected        final DateFormat      dateFormat           = new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat);
   public    static final String          enteringOntology     = className + ":Entering";
   protected        final AID             heatingZoneAgentId   = new AID();
   public    static final String          leavingOntology      = className + ":Leaving";
   protected        final AID             lightingZoneAgentId  = new AID();
   private          final MessageTemplate messageTemplate      = MessageTemplate.or(MessageTemplate.MatchOntology(enteringOntology),
                                                                                    MessageTemplate.MatchOntology(leavingOntology));
   public    static final String          occupancyOntology    = className + ":Occupancy";
   protected        final Set<String>     occupantSet          = new HashSet<String>();
   private   static final long            serialVersionUID     = -7605004311858914283L;

   protected void setup()
      {
      super.setup();
      capacity = extendedProperties.getIntProperty("Capacity", 0);
      coolingZoneAgentId.setLocalName(extendedProperties.getProperty("CoolingZoneAgentLocalName", "Unknown cooling zone agent for " + getLocalName()));
      heatingZoneAgentId.setLocalName(extendedProperties.getProperty("HeatingZoneAgentLocalName", "Unknown heating zone agent for " + getLocalName()));
      lightingZoneAgentId.setLocalName(extendedProperties.getProperty("LightingZoneAgentLocalName", "Unknown lighting zone agent for " + getLocalName()));
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
            try
               {
            // final String content         = inboundMessage.getContent();
               final int    deltaOccupancy;
               final String ontology          = inboundMessage.getOntology();
               final String senderAgentName   = inboundMessage.getSender().getLocalName();
               final Date   simulatedDateTime = (Date) inboundMessage.getContentObject();

               // if (content.startsWith(PersonAgent.entering))
               if (enteringOntology.equals(ontology))
                  {
                  logger.debug("Entering:  " + senderAgentName);
                  deltaOccupancy = 1;
                  occupantSet.add(senderAgentName);
                  }
               else
                  {
                  if (leavingOntology.equals(ontology))
               // if (content.startsWith(PersonAgent.leaving))
                     {
                     logger.debug("Leaving:  " + senderAgentName);
                     deltaOccupancy = -1;
                     occupantSet.remove(senderAgentName);
                     }
                  else
                     {
                     logger.warn("Received unexpected ontology:  " + ontology);
                     deltaOccupancy = 0;
                     }
                  }
               /*
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
               */

               final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

               outboundMessage.addReceiver(coolingZoneAgentId);
               outboundMessage.addReceiver(heatingZoneAgentId);
               outboundMessage.addReceiver(lightingZoneAgentId);
               outboundMessage.setContentObject(new OccupancyChange(deltaOccupancy,
                                                                    simulatedDateTime));
               outboundMessage.setOntology(EnvironmentalZoneAgent.occupancyOntology);
               send(outboundMessage);
               }
            catch (final IOException ioException)
               {
               logger.error("Unable to set occupancy change content:  " + ioException.getMessage(),
                            ioException);
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         logger.trace("End   MonitorOccupancyBehaviour.action");
         }
      }

   public class OccupancyChange
      implements Serializable
      {
      public final int  deltaOccupancy;
      public final Date simulatedDateTime;

      public OccupancyChange(final int deltaOccupancy,
                             final Date simulatedDateTime)
         {
         this.deltaOccupancy = deltaOccupancy;
         this.simulatedDateTime = simulatedDateTime;
         }
      }
   }
