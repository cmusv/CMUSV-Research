package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.controller.LocalityDAOInterface;
import edu.cmu.smartcommunities.database.controller.hibernate.DAOFactory;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CreateSerializedLocalityFile
   {
   private static final DAOFactory           daoFactory       = new DAOFactory();
   private static final String               fileName         = "/home/mcsmith/Locality.data";
   private static final LocalityDAOInterface localityDAO      = daoFactory.getLocalityDAO();
   private              Locality             root             = null;

   private void createLocalities()
      {
      Locality nasaAmesResearchCenter = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "NASA Ames Research Center"   , null                  ,    0D);
      Locality sustainabilityBase     = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Sustainability Base"         , nasaAmesResearchCenter,    0D);
      Locality northBuilding          = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "North Building"              , sustainabilityBase    ,    0D);
      Locality nbHeatingZone          = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Heating Zone"                , northBuilding         ,    0D);
      Locality nbf0CoolingZone        = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Floor 0 Cooling Zone"        , nbHeatingZone         ,    0D);

      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 0 Lighting Zone", nbf0CoolingZone       , 2400D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 1 Lighting Zone", nbf0CoolingZone       ,  800D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 2 Lighting Zone", nbf0CoolingZone       ,  240D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 3 Lighting Zone", nbf0CoolingZone       ,  160D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 4 Lighting Zone", nbf0CoolingZone       ,  160D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 5 Lighting Zone", nbf0CoolingZone       ,  160D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 6 Lighting Zone", nbf0CoolingZone       ,  160D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 4 Lighting Zone"  , nbf0CoolingZone       , 1200D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 5 Lighting Zone"  , nbf0CoolingZone       , 1200D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 6 Lighting Zone"  , nbf0CoolingZone       , 1200D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 7 Lighting Zone"  , nbf0CoolingZone       , 1200D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 8 Lighting Zone"  , nbf0CoolingZone       , 1200D));
      nbf0CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 9 Lighting Zone"  , nbf0CoolingZone       , 1200D));

      Locality nbf1CoolingZone        = new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Floor 1 Cooling Zone"        , nbHeatingZone         ,    0D);

      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 0 Lighting Zone", nbf1CoolingZone       ,  800D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 1 Lighting Zone", nbf1CoolingZone       ,  240D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 2 Lighting Zone", nbf1CoolingZone       ,  240D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 3 Lighting Zone", nbf1CoolingZone       ,  240D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 4 Lighting Zone", nbf1CoolingZone       ,  160D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 5 Lighting Zone", nbf1CoolingZone       ,  160D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Meeting Room 6 Lighting Zone", nbf1CoolingZone       ,  240D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 3 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 4 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 5 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 6 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 7 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 8 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbf1CoolingZone.getChildLocalitySet().add(new Locality(new HashSet<Locality>(), new HashSet<Measurement>(), "Work Space 9 Lighting Zone"  , nbf1CoolingZone       , 1200D));
      nbHeatingZone.getChildLocalitySet().add(nbf0CoolingZone);
      nbHeatingZone.getChildLocalitySet().add(nbf1CoolingZone);

      writeToDisc();
      }
   private void appendToList(final int                   level,
                             final List<LeveledLocality> leveledLocalityList,
                             final Locality              locality)
      {
      System.out.println("Adding " + locality.getName());
      leveledLocalityList.add(new LeveledLocality(level,
                                                  locality));
      for (Locality childLocality:  locality.getChildLocalitySet())
         {
         appendToList(level + 3,
                      leveledLocalityList,
                      childLocality);
         }
      }

   public void loadLocalities()
      {
      /*
      final Session session = daoFactory.getCurrentSession();

      session.beginTransaction();
      print(0,
            localityDAO.findById(1l, false));
      session.getTransaction().commit();
      */
      final List<LeveledLocality> leveledLocalityList = new Vector<>();
      final Session               session             = daoFactory.getCurrentSession();
      final Transaction           transaction         = session.getTransaction();

      try
         {
         System.out.println("before begin(), Transaction is active:  " + transaction.isActive());
         transaction.begin();
         System.out.println("after  begin(), Transaction Is Active:  " + transaction.isActive());
         root = localityDAO.findById(1L, false);
         System.out.println("After findById");
         appendToList(0,
                      leveledLocalityList,
                      localityDAO.findById(1L, false));
         writeToDisc();
         transaction.commit();
         }
      catch (final Exception exception)
         {
         if (transaction != null)
            {
            transaction.rollback();
            }
         exception.printStackTrace();
         }
      finally
         {
         if (session.isOpen())
            {
            session.close();
            }
         }
    }

   public static void main(final String[] argument)
      {
   // new CreateSerializedLocalityFile().loadLocalities();
      new CreateSerializedLocalityFile().createLocalities();
      }

   public void writeToDisc()
      {
      try
         {
         OutputStream fileOutputStream     = new FileOutputStream(fileName);
         OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
         ObjectOutput objectOutputStream   = new ObjectOutputStream(bufferedOutputStream);

         try
            {
            objectOutputStream.writeObject(root);
            }
         finally
            {
            objectOutputStream.close();
            bufferedOutputStream.close();
            fileOutputStream.close();
            }
         }
      catch (final IOException ioException)
         {
         ioException.printStackTrace();
         }
      }

   private static class LeveledLocality
      {
      public final int      level;
      public final Locality locality;

      public LeveledLocality(final int      level,
                             final Locality locality)
         {
         this.level    = level;
         this.locality = locality;
         }
      }
   }
