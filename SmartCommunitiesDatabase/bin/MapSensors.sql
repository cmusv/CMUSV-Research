SELECT Locality.localityId,
       Locality.name,
       MeasurementType.name,
       Sensor.sensorId
  FROM Locality,
       MeasurementType,
       SymbolicLocation,
       SensorPlatformLocation,
       Sensor
 WHERE Locality.localityId = SymbolicLocation.localityId AND
       SymbolicLocation.sensorPlatformLocationId = SensorPlatformLocation.sensorPlatformLocationId AND
       SensorPlatformLocation.sensorPlatformId = Sensor.sensorPlatformId AND
       Sensor.measurementTypeId = MeasurementType.measurementTypeId
 ORDER BY Locality.localityId,
          MeasurementType.name;
