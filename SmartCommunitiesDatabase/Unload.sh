#!/bin/bash
mysql --user=smartspaces --password=cmucmu SmartCommunities <<EOF >Measurement.data
SELECT carbonDioxide, humidity, light, localityId, measurementDateTime, occupancy, temperature, watts
  FROM Measurement
 WHERE localityId <= 70 AND
       DATE(measurementDateTime) IN ("2012-06-14", "2012-06-21", "2012-06-25", "2012-06-28");
EOF
cat <<EOF >Measurement.awk
      {
      if (count % 1000 == 0)
         {
         printf("INSERT\n");
         printf("  INTO Measurement (carbonDioxide, humidity, light, localityId, measurementDateTime, occupancy, temperature, watts)\n");
         }
      printf("%s (%s, %s, %s, %s, \"%s %s\", %s, %s, %s)%s\n",
             count % 1000 == 0 ? "VALUES" : "      ",
             \$1,
             \$2,
             \$3,
             \$4,
             \$5,
             \$6,
             \$7,
             \$8,
             \$9,
             count % 1000 == 999 ? ";" : ",");
      count++;
      }
EOF
awk -f Measurement.awk <Measurement.data >Measurement.sql
rm Measurement.awk
