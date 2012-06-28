package edu.cmu.smartcommunities.simulation.model;

import edu.cmu.smartcommunities.simulation.visualization.AbstractTreeTableModel;
import edu.cmu.smartcommunities.simulation.visualization.TreeTableModel;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class SimulationModel
   extends AbstractTreeTableModel
   implements Serializable
   {
   public /* private */              int                          columnCount        = 24 * 2 + 1;
   public /* private */ static final String                       electricityHeading = "E";
   public /* private */ static final String                       fileName           = "/home/mcsmith/SimulationModel.file";
   public /* private */              int                          granularity        = 1;
   public /* private */ static final String                       locationHeading    = "Location";
   public /* private */ static final String                       occupancyHeading   = "O";
   public /* private */        final ElectricityConsumerInterface rootNode;
   public /* private */ static       SimulationModel              simulationModel    = null;
   public /* private */              Date                         startDateTime      = new Date(2012 - 1900, Calendar.JANUARY, 03, 00, 00, 00); // TODO:  Need to make this more flexible
   
   public /* private */ SimulationModel()
      {
	   super(new ElectricityConsumer("mcsmith-mcsmith-NasaAmesResearchCenterAgent", "NASA Ames Research Center"));
	   System.out.println("Begin SimulationModel.<init>");
	   rootNode = (ElectricityConsumerInterface) getRoot();
	   ElectricityConsumer sustainabilityBase = new ElectricityConsumer("mcsmith-mcsmith-SustainabilityBaseAgent", "Sustainability Base");
      ElectricityConsumer building           = new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingAgent", "North Building");
      ElectricityConsumer heatingZone        = new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingHeatingZoneAgent", "Heating Zone");
      ElectricityConsumer coolingZone        = new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0CoolingZoneAgent", "Floor 0 Cooling Zone");

      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone", 2400D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone",  160D));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone"));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone"));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone"));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone"));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", 1200D));
/**/  coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone"));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", 1200D));
      heatingZone.add(coolingZone);
      coolingZone = new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1CoolingZoneAgent", "Floor 1 Cooling Zone");
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone",  240D));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone"));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone"));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone"));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", 1200D));
      heatingZone.add(coolingZone);
      building.add(heatingZone);
      sustainabilityBase.add(building);
      building = new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingAgent", "South Building");
      heatingZone = new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingHeatingZoneAgent", "Heating Zone");
      coolingZone = new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0CoolingZoneAgent", "Floor 0 Cooling Zone");
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", 1200D));
   // coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone"));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone",  720D));
      heatingZone.add(coolingZone);
      coolingZone = new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1CoolingZoneAgent", "Floor 1 Cooling Zone");
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone",  240D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone",  160D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone",  240D));
/**/  coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone"));
/**/  coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone"));
/**/  coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone"));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone",  600D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", 1200D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone",  800D));
      coolingZone.add(new ElectricityConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", 1200D));
      heatingZone.add(coolingZone);
      building.add(heatingZone);
      sustainabilityBase.add(building);
      rootNode.add(sustainabilityBase);
      }

   public static SimulationModel getInstance()
      {
      if (simulationModel == null)
         {
      // simulationModel = new SimulationModel(new ElectricityConsumer("mcsmith-mcsmith-", "Sustainability Base"));
         simulationModel = new SimulationModel();
         }
      return simulationModel;
      }

   public void fireTreeNodesChanged(ElectricityConsumerInterface electricityConsumer)
      {
            ElectricityConsumerInterface        parent      = electricityConsumer.getParent();
      final Stack<ElectricityConsumerInterface> parentStack = new Stack<ElectricityConsumerInterface>();

      if (electricityConsumer.getParent() != null)
         {
         while (parent != null)
            {
            parentStack.push(parent);
            parent = parent.getParent();
            }
         fireTreeNodesChanged(electricityConsumer,
                              parentStack.toArray(),
                              new int[]{},
                              electricityConsumer.getChildren().toArray());
         }
      }

   //
   // The TreeModel interface
   //

   public Object getChild(Object node,
                          int i)
      {
      return ((ElectricityConsumerInterface) node).getChildren().get(i); 
      }

   public int getChildCount(Object node)
      {
      return ((ElectricityConsumerInterface) node).getChildren().size();
      }

   //
   // The TreeTableNode interface. 
   //

   @Override
   public Class<?> getColumnClass(int column)
      {
      return column == 0 ? TreeTableModel.class : Integer.class;
      }

   public int getColumnCount()
      {
	   return columnCount;
      }

   public String getColumnName(int column)
      {
	   return column == 0 ? locationHeading : column % 2 == 0 ? occupancyHeading : electricityHeading;
	   }

   public Object getValueAt(final Object node,
                            final int    column)
      {
      final ElectricityConsumerInterface electricityConsumer = (ElectricityConsumerInterface) node;
      final int                          hour                = (column - 1) / 2 * granularity;
      final Date                         dateTime            = new Date(startDateTime.getTime() + (3600 * 1000 * hour));

      return column     == 0 ? electricityConsumer :
             column % 2 == 0 ? electricityConsumer.getTotalOccupancy(dateTime, granularity) / granularity:
                               electricityConsumer.getTotalElectricityConsumption(dateTime, granularity);
      }

   public void initializeState(final Date dateTime)
      {
            ElectricityConsumerInterface        electricityConsumer      = (ElectricityConsumerInterface) getRoot();
      final Queue<ElectricityConsumerInterface> electricityConsumerQueue = new ArrayDeque<ElectricityConsumerInterface>();

      electricityConsumerQueue.add(electricityConsumer);
      while ((electricityConsumer = electricityConsumerQueue.poll()) != null)
         {
         electricityConsumer.initializeState(dateTime);
         electricityConsumerQueue.addAll(electricityConsumer.getChildren());
         }
      }

   public void setColumnCount(final int columnCount)
      {
      this.columnCount = columnCount;

   // final ElectricityConsumerInterface rootNode = (ElectricityConsumerInterface) getRoot();

      fireTreeStructureChanged(rootNode,
                               new Object[] { rootNode },
                               null, // new int[] { 0, 1 },
                               null); // rootNode.getChildren().toArray());
      }

   public void setGranularity(final int granularity)
      {
      this.granularity = granularity;

   // final ElectricityConsumerInterface rootNode = (ElectricityConsumerInterface) getRoot();

      fireTreeStructureChanged(rootNode,
                               new Object[] { rootNode },
                               null, // new int[] { 0, 1 },
                               null); // rootNode.getChildren().toArray());
      }

   public void setStartDateTime(final Date startDateTime)
      {
      this.startDateTime = startDateTime;
      
   // final ElectricityConsumerInterface rootNode = (ElectricityConsumerInterface) getRoot();

      fireTreeStructureChanged(rootNode,
                               new Object[] { rootNode },
                               null, // new int[] { 0, 1 },
                               null); // rootNode.getChildren().toArray());
      }
/*
   public void setValueAt(final Object value,
                          final Object node,
                          final int    column)
      {
      throw new IllegalStateException("Not implemented, probably permanently.");
      }
*/

   public static void readFromDisc()
      {
      System.out.println("Begin readFromDisc");
      try
         {
         InputStream fileInputStream     = new FileInputStream(fileName);
      // InputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
      // ObjectInput objectInputStream   = new ObjectInputStream(bufferedInputStream);
      ///XMLDecoder  xmlDecoder          = new XMLDecoder(fileInputStream);
         FileReader     fileReader       = new FileReader(fileName);
         BufferedReader bufferedReader   = new BufferedReader(fileReader);

         try
            {
         // simulationModel = (SimulationModel) objectInputStream.readObject();
         ///simulationModel = (SimulationModel) xmlDecoder.readObject();
            simulationModel = new SimulationModel();
            String data = null;

            while ((data = bufferedReader.readLine()) != null)
               {
            // StringTokenizer stringTokenizer = new StringTokenizer(data, ",");

            // ElectricityConsumer electricityConsumer = ElectricityConsumer.getElectricityConsumer(stringTokenizer.nextToken());
               ElectricityConsumer electricityConsumer = ElectricityConsumer.getElectricityConsumer(data.substring(0, data.indexOf(",")));

               electricityConsumer.fromLongString(data.substring(data.indexOf(",") + 1));
               }
            }
      // catch (ClassNotFoundException e)
      //    {
      //    e.printStackTrace();
      //    }
         finally
            {
         // objectInputStream.close();
         ///xmlDecoder.close();
            }
         }
      catch (FileNotFoundException e)
         {
         e.printStackTrace();
         }
      catch (IOException e)
         {
         e.printStackTrace();
         }
      System.out.println("End   readFromDisc");
      }

   public static void preWrite(final ElectricityConsumerInterface electricityConsumer,
                               final List<ElectricityConsumerInterface> electricityConsumerList)
      {
      electricityConsumerList.add(electricityConsumer);
      for (ElectricityConsumerInterface child:  electricityConsumer.getChildren())
         {
         preWrite(child, electricityConsumerList);
         }
      }

   public static void writeToDisc()
      {
      try
         {
         OutputStream fileOutputStream     = new FileOutputStream(fileName);
      // OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
      // ObjectOutput objectOutputStream   = new ObjectOutputStream(bufferedOutputStream);
      ///XMLEncoder   xmlEncoder           = new XMLEncoder(fileOutputStream);
         List<ElectricityConsumerInterface> list = new Vector<>();

      // preWrite(simulationModel.rootNode, list);
         try
            {
         // objectOutputStream.writeObject(simulationModel);
         ///xmlEncoder.writeObject(simulationModel);
         // for (ElectricityConsumerInterface electricityConsumer:  list)
            for (String agentLocalName:  ElectricityConsumer.agentNameMap.keySet())
               {
               fileOutputStream.write((agentLocalName + "," + ElectricityConsumer.getElectricityConsumer(agentLocalName).toLongString()).getBytes());
               }
            }
         finally
            {
         // objectOutputStream.close();
         // bufferedOutputStream.close();
         ///xmlEncoder.close();
            fileOutputStream.close();
            }
         }
      catch (final IOException ioException)
         {
         ioException.printStackTrace();
         }
      }
   }
