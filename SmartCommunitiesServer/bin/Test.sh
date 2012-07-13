#ServletHost=tesla.sv.cmu.edu
ServletHost=mcsmith-z600
#curl -X PUT http://${ServletHost}/SmartCommunitiesServer/servlet?localityId=126\&measurementDateTime=201206042047\&light=0.0\&humidity=42.1\&temperature=20.8             && echo
curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=126\&count=4320\&measure=temperature                                   && echo
curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=126\&count=4320\&measure=temperature\&measurementDateTime=201207042359 && echo
#curl -X PUT http://${ServletHost}/SmartCommunitiesServer/servlet?localityId=50\&measurementDateTime=201206072118\&occupancy=0\&watts=0.0                                  && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=7\&count=4320\&measurementDateTime=201201040800\&measure=occupancy        && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=7\&count=4320\&measurementDateTime=201201040800\&measure=watts            && echo
#curl -X PUT http://${ServletHost}/SmartCommunitiesServer/servlet?localityId=126\&measurementDateTime=201106111926\&light=716.4\&humidity=26.0\&temperature=24.0              && echo
#curl -X PUT http://${ServletHost}/SmartCommunitiesServer/servlet?localityId=126\&measurementDateTime=201106111926\&light=816.4\&humidity=126.0\&temperature=124.0              && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=126\&count=60\&measurementDateTime=201106112000\&measure=light            && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=126\&count=60\&measurementDateTime=201106112000\&measure=humidity         && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=126\&count=60\&measurementDateTime=201106112000\&measure=temperature      && echo
#curl -X PUT http://${ServletHost}/SmartCommunitiesServer/servlet?localityId=6\&measurementDateTime=201206011200\&carbonDioxide=1\&humidity=0.5\&light=0\&occupancy=1\&temperature=22\&watts=240
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=carbonDioxide && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=humidity      && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=light         && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=occupancy     && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=temperature   && echo
#curl -X GET http://${ServletHost}/SmartCommunitiesServer/servlet?resource=measurements\&localityId=6\&measurementDateTime=201206011230\&count=60\&measure=watts         && echo
