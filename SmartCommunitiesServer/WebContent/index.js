var hours               = 36;
var localityId          = 1;
var maximumHours        = 72;
var measurementsPerHour = 60;
var maximumMeasurements = maximumHours * measurementsPerHour; 
var occupancy           = new Array(maximumMeasurements);
var urlBegin            = "http://" + window.location.hostname + ":" + window.location.port + "/SmartCommunitiesServer/servlet?";
var wattsConsumed       = new Array(maximumMeasurements);

function debug(message)
   {
   document.getElementById("debugSpan").innerHTML = message;
   }

function drawGraph(canvasId,
                   configuration,
                   data)
   {
   var label     = new Array();
   var lineGraph = new RGraph.Line(canvasId,
                                   data.slice(0 - hours * measurementsPerHour));
   var startDate = new Date();

	startDate.setTime(startDate.getTime() - hours * (60 * 60 * 1000) - (60 * 1000));
   for (var minute = 0; minute < hours * 60; minute++)
   	{
   	startDate.setTime(startDate.getTime() + 60 * 1000);
   	if (startDate.getMinutes() == 0)
   		{
   		if (hours < 24)
   			{
   			label[minute] = startDate.getHours() + ":00";
   			}
   		else
   			{
   			if (startDate.getHours() % 12 == 0)
   				{
   				label[minute] = (startDate.getMonth() + 1) + "/" + startDate.getDate() + " " + startDate.getHours() + ":00";
   				}
   			else
   				{
   				label[minute] = null;
   				}
   			}
   		}
   	else
   		{
   		label[minute] = null;
   		}
   	}
   RGraph.Clear(lineGraph.canvas);
   RGraph.SetConfig(lineGraph,
                    configuration);
   lineGraph.Set("chart.filled",        true);
   lineGraph.Set("chart.gutter.bottom", 20);
   lineGraph.Set("chart.gutter.left",   50);
   lineGraph.Set("chart.labels",        label);
   lineGraph.Set("chart.xticks",        0.00001);
   lineGraph.Draw();
   }

function getLeveledLocalities()
   {
   var url            = urlBegin + "resource=leveledLocalities"; 
   var xmlHttpRequest = new XMLHttpRequest();

   xmlHttpRequest.onreadystatechange = function()
      {
      switch (xmlHttpRequest.status)
         {
         case 0:
            {
            break;
            }
         case 200:
            {
            if (xmlHttpRequest.readyState == 4)
               {
               var leveledLocality           = xmlHttpRequest.responseText.split("\n");
               var localityIdSelectInnerHtml = "";
               var spacesIndentedPerLevel    = 3;

               for (var i = 0; i < leveledLocality.length - 1; i++)
                  {
                  var attribute = leveledLocality[i].split(",");
                  var level     = attribute[0];

                  localityIdSelectInnerHtml += "                  <option value=\"" + attribute[1] + "\">";
                  for (var j = level * spacesIndentedPerLevel; j > 0; j--)
                     {
                     localityIdSelectInnerHtml += "&nbsp";
                     }
                  localityIdSelectInnerHtml += attribute[2] + "</option>\n";
            	   }
               document.getElementById("localityIdSelect").innerHTML = localityIdSelectInnerHtml;
               }
            break;
            }
         default:
            {
            alert("HTTP URL:     " + url + "\n" +
                  "HTTP status:  " + xmlHttpRequest.status);
            }
         };
      };
   xmlHttpRequest.open("GET", url, false);
   xmlHttpRequest.send();
   }

function getMeasurement(canvasId,
                        configuration,
                        localityId,
                        timePeriod,
                        measurementType)
   {
   var url            = urlBegin + "resource=measurements&localityId=" + localityId + "&count=" + hours * 60 + "&measure=" + measurementType;
   var xmlHttpRequest = new XMLHttpRequest();

   xmlHttpRequest.onreadystatechange = function()
      {
      switch (xmlHttpRequest.status)
         {
         case 0:
            {
            break;
            }
         case 200:
            {
            if (xmlHttpRequest.readyState == 4)
               {
               drawGraph(canvasId,
                         configuration,
                         xmlHttpRequest.responseText.split(","));
               }
            break;
            }
         default:
            {
            alert("HTTP URL:     " + url + "\n" +
                  "HTTP status:  " + xmlHttpRequest.status);
            }
         }
      };
   xmlHttpRequest.open("GET", url, true);
   xmlHttpRequest.send();
   }

function getMeasurements(localityId,
                         timePeriod)
   {
   var occupancyGraphConfiguration =
      {
      "chart.colors":  ["blue"],
      };
   var wattsGraphConfiguration =
      {
      "chart.colors":  ["yellow"],
      };
   
   getMeasurement("occupancyCanvas",
                  occupancyGraphConfiguration,
                  localityId,
                  timePeriod,
                  "simulatedOccupancy");
   getMeasurement("wattsConsumedCanvas",
                  wattsGraphConfiguration,
                  localityId,
                  timePeriod,
                  "simulatedWatts");
   }

function getSelectedValue(select)
   {
   return select.options[select.selectedIndex].value;
   }

function initialize()
   {
// document.getElementById("hoursSelect") // TODO:  Reset to 36 hours, or something.
   getLeveledLocalities();
   getMeasurements(localityId, hours);
   }

function setHours(hoursSelect)
   {
   hours = getSelectedValue(hoursSelect);
   getMeasurements(localityId, hours);
   }

function setLocalityId(localityIdSelect)
   {
   localityId = getSelectedValue(localityIdSelect);
   getMeasurements(localityId, hours);
   }
