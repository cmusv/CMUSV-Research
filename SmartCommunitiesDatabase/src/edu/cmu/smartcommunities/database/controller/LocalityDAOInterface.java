package edu.cmu.smartcommunities.database.controller;

import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import java.util.Date;

public interface LocalityDAOInterface
   extends DAOInterface<Locality, Long>
   {
   public Locality getLocality(final String fullyQualifiedName);
   public Measurement getMeasurement(final Locality locality,
                                     final Date     dateTime);
   public void setLocalOccupancy(final Locality locality,
                                 final Date     dateTime,
                                 final int      localOccupancy);
   public Locality loadLocalities();
   }
