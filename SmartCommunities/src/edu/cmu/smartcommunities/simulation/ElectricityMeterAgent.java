package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class ElectricityMeterAgent
   extends Agent
   {
   private static final String          className                     = ElectricityMeterAgent.class.getName();
   public  static final String          electricalConsumptionOntology = className + ":ElectricalConsumption";
   private static final MessageTemplate messageTemplate               = MessageTemplate.MatchOntology(electricalConsumptionOntology);
   private static final long            serialVersionUID              = 6894816029239182806L;
/*
   private        final Consumer        sustainabilityBase            = new Consumer(null, null, "Sustainability Base");

   public ElectricityMeterAgent()
      {
      final Consumer northBuilding = new Consumer(null, sustainabilityBase, "North Building");
      sustainabilityBase.childConsumerSet.add(northBuilding);
      final Consumer southBuilding = new Consumer(null, sustainabilityBase, "South Building");
      sustainabilityBase.childConsumerSet.add(southBuilding);
      }
*/
   protected void setup()
      {
      super.setup();
      addBehaviour(new MonitorElectricityUsageBehaviour(this));
      }

   private class MonitorElectricityUsageBehaviour
      extends CyclicBehaviour
      {
      private static final long serialVersionUID = 8209584853702801273L;

      public MonitorElectricityUsageBehaviour(final Agent agent)
         {
         super(agent);
         }

      @Override
      public void action()
         {
         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            try
               {
               final String user  = inboundMessage.getSender().getLocalName();
               final Usage  usage = (Usage) inboundMessage.getContentObject();

               logger.debug("Electrical consumption:  user = " + user +
                            ", load = " + usage.load +
                            ", time = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(usage.usageDateTime));
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get electrical consumption content:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         }
      }

   public static class Usage
      implements Serializable
      {
      public         final int  load;
      private static final long serialVersionUID  = 7336733381269062913L;
      public         final Date usageDateTime;

      public Usage(final int  load,
                   final Date usageDateTime)
         {
         this.load = load;
         this.usageDateTime = usageDateTime;
         }
      }
/*
   public class Consumer
      {
      public final Set<Consumer> childConsumerSet   = new TreeSet<Consumer>();
      public final String        fullyQualifiedName;
      public final Consumer      parent;
      public final String        shortName;

      public Consumer(final String   fullyQualifiedName,
                      final Consumer parent,
                      final String   shortName)
         {
         this.fullyQualifiedName = fullyQualifiedName;
         this.parent = parent;
         this.shortName = shortName;
         }

      public Consumer findConsumerByName(final String name)
         {
         Consumer theConsumer = null;

         for (Consumer consumer:  childConsumerSet)
            {
            if (consumer.shortName.equals(name))
               {
               theConsumer = consumer;
               break;
               }
            else
               {
               consumer.findConsumerByName(name);
               }
            }
         return theConsumer;
         }
      }
*/
   }
