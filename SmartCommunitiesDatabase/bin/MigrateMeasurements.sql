INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 1, carbonDioxide
  FROM OldMeasurement
 WHERE carbonDioxide IS NOT NULL;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 2, humidity
  FROM OldMeasurement
 WHERE humidity IS NOT NULL;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 3, light
  FROM OldMeasurement
 WHERE light IS NOT NULL;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 4, occupancy
  FROM OldMeasurement
 WHERE occupancy IS NOT NULL AND localityId > 70;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 5, occupancy
  FROM OldMeasurement
 WHERE occupancy IS NOT NULL AND localityId <= 70;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 6, watts
  FROM OldMeasurement
 WHERE watts IS NOT NULL;
INSERT
  INTO Measurement (localityId, measurementDateTime, measurementTypeId, value)
SELECT localityId, measurementDateTime, 7, temperature
  FROM OldMeasurement
 WHERE temperature IS NOT NULL;
