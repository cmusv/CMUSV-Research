package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumer;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumerInterface;
import edu.cmu.smartcommunities.simulation.model.SimulationModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ElectricityMeterAgent
   extends Agent
   {
   private static final String          className                     = ElectricityMeterAgent.class.getName();
   public  static final String          electricalConsumptionOntology = className + ":ElectricalConsumption";
   private static final MessageTemplate messageTemplate               = MessageTemplate.MatchOntology(electricalConsumptionOntology);
   private static final long            serialVersionUID              = 6894816029239182806L;

   protected void setup()
      {
      super.setup();
      addBehaviour(new MonitorElectricityUsageBehaviour(this));
      addBehaviour(new ProcessExternalRequestsBehaviour(this));
      }

   private class MonitorElectricityUsageBehaviour
      extends CyclicBehaviour
      {
      private              int  messagesReceived = 0;
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
            logger.debug("Messages received:  " + ++messagesReceived);
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

   private class ProcessExternalRequestsBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate = MessageTemplate.MatchSender(new AID("mcsmith-mcsmith-SocketProxyAgent", AID.ISLOCALNAME));
      private        final SimulationModel simulationModel;

      public ProcessExternalRequestsBehaviour(final Agent agent)
         {
         super(agent);
         simulationModel = SimulationModel.getInstance();
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
            final String content = inboundMessage.getContent();
            final int    i       = content.lastIndexOf(" ");
            final String locality = content.substring(0, i);
            final int    measurements = Integer.parseInt(content.substring(i + 1));

            logger.debug("External request received:  " + content);
            logger.debug("Locality:  " + locality);
            logger.debug("Measurements:  " + measurements);

            final StringBuffer stringBuffer = new StringBuffer();

            for (int hour = 0; hour < measurements / 60; hour++)
               {
               for (int minute = 0; minute < 30; minute++)
                  {
                  stringBuffer.append("" + minute + " " + minute * 40 + "\n");
                  }
               for (int minute = 30; minute < 60; minute++)
                  {
                  stringBuffer.append("" + (60 - minute) + " " + (60 - minute) * 40 + "\n");
                  }
               }

         // final ElectricityConsumerInterface electricityConsumer = ElectricityConsumer.getElectricityConsumer(locality);
            // get last time in model
            // for each hour
            //    getTotalWattsConsumed for the hour
            //    getTotalOccupancy for the hour
            //    for 1..60
            //       construct reply part
            //    next
            // next
            // reply

            final ACLMessage outboundMessage    = inboundMessage.createReply();
            final String     outboundContent    = stringBuffer.toString();
            final int        outboundContentEnd = outboundContent.length() - 1;
            final int        safeBufferSize     = 5000;

            for (int begin = 0; begin < outboundContent.length(); begin += safeBufferSize)
               {
               final String beginToken = begin == 0 ? "$" : "";
               final int    end        = Math.min(begin + safeBufferSize, outboundContentEnd);
               final String endToken   = end == outboundContentEnd ? "$" : ""; 
               final String outboundContentSubstring = beginToken + outboundContent.substring(begin, end) + endToken;

               System.out.println("outboundContentSubstring:  >" + outboundContentSubstring + "<");
               outboundMessage.setContent(outboundContentSubstring);
               final String dummy = outboundMessage.toString();
               System.out.println("ACL message representation is " + dummy.length() + " bytes");
               send(outboundMessage);
               }
            /*
            System.out.println("outboundContent:  >" + outboundContent + "<");
            outboundMessage.setContent(outboundContent);
            final String dummy = outboundMessage.toString();
            System.out.println("ACL message representation is " + dummy.length() + " bytes");
            send(outboundMessage);
            */
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
   }
