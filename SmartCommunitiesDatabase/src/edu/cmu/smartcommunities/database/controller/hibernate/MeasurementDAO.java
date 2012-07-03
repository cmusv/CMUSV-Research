package edu.cmu.smartcommunities.database.controller.hibernate;

import edu.cmu.smartcommunities.database.controller.MeasurementDAOInterface;
import edu.cmu.smartcommunities.database.model.Measurement;
import java.util.List;

public class MeasurementDAO
   extends AbstractDAO<Measurement, Long>
   implements MeasurementDAOInterface
   {
   @Override
   public List<Measurement> findByExample(final Measurement exampleInstance,
                                          final String[]    fexcludeProperty)
      {
      throw new IllegalStateException("Not yet implemented"); // TODO Auto-generated method stub
   // return null;
      }
   }
