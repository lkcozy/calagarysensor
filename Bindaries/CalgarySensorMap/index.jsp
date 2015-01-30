<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!doctype html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CalgarySenors</title>
    <link rel="stylesheet" href="css/foundation.css" />
    <link rel="stylesheet" href="css/map.css" />
    <script src="https://maps.googleapis.com/maps/api/js?v=3&amp;sensor=false"></script>
    <script type="text/javascript" src="js/markerclusterer.js"></script>
    <script type="text/javascript" src="js/infobox.js"></script>
    <script type="text/javascript" src="js/mqtt/mqttws31.js"></script>
    <script type="text/javascript" src="js/mqtt/config.js"></script>
    <script type="text/javascript">
    	// google map
        var map;
    	// center of calgary
        var calgary = new google.maps.LatLng(51.048382, -114,0709591);
        // pop-up window
        var infoBox;
        // total sensor number
        var SENSOR_NUM = 1000; 
        // track fade out duration (seconds)
        var FADE_OUT_DURATION = 10; 
        // interval to request sensor information  
        var REFRESH_RATE = 1;
        // store all sensors' track 
        var sensorPath = new Array();
        // The grid size of a cluster in pixels
        var CLUSTER_SIZE = 40;
        // The maximum zoom level at which clustering is enabled 
        var CLUSTER_ZOOM = 10;
        // store all sensor' latest information
        var markers = [];
        //  google map marker cluster
        var markerCluster;
        // js timer
        var getInfoTimer;
        // server host
        var SERVER_HOST = 'http://159.226.111.19:8080/calgarysensorserver/';
        //initialize webapp
        function initialize() 
        {
          initMap();
          initInfoBox();
          initSensorTrack();
        }
      
        function initMap()
        {
          var mapOptions = {
              center: calgary,
              mapTypeId: google.maps.MapTypeId.ROADMAP,
              zoom: 11,
              zoomControl:true,
              zoomControlOptions: {
                style:google.maps.ZoomControlStyle.SMALL
              }
            };   
          
          map = new google.maps.Map(document.getElementById('map'), mapOptions);
        }
    	// initalize pop-up infowindow
        function initInfoBox()
       {
        var infoOptions = {
          disableAutoPan: false,
          maxWidth: 0,
          pixelOffset: new google.maps.Size(-140, 0),
          zIndex: null,
          boxStyle: { 
            background: "url('img/tipbox.gif') no-repeat",
            opacity: 0.75,
            width: "280px"},
          closeBoxMargin: "10px 2px 2px 2px",
          closeBoxURL: "img/close.gif",
          infoBoxClearance: new google.maps.Size(1, 1),
          isHidden: false,
          pane: "floatPane",
          enableEventPropagation: false
        };
        infoBox = new InfoBox(infoOptions);
       }
    	// initialize all senosrs' track array
       function initSensorTrack()
       {
        var polyOptions = {
              strokeColor : 'blue',
              strokeOpacity : 0.5,
              strokeWeight : 2
            };
    
          for(var i = 0; i < SENSOR_NUM;i++)
          {
        	  sensorPath[i]=[];
          }
       }
      
        //pop-up info window
        function markerClick(marker)
        {
           google.maps.event.addListener(marker, "click", function(){
            setInfoBoxContent(marker);
            infoBox.open(map,marker);
          });
        }
		// set the infobox's content
       function setInfoBoxContent(marker)
       {
          var boxText = document.createElement("div");
          boxText.style.cssText = "border: 1px solid black; margin-top: 8px; background: white; padding: 5px;";
        
          boxText.innerHTML = "<strong> This is the sensor <font size='4px' color='blue'>"+ marker.id+"</font></strong><br/>"
          + "<strong>The location of this sensor is at <font size='4px' color='blue'>(" + marker.getPosition().toUrlValue() + ")</font></strong><br/>"
          + "<strong>The observed temperature is <font size='4px' color='blue'>" + marker['temp'] + "</font> degrees</strong>";
          infoBox.setContent(boxText);
        
       }
		// control the stimulation
       function control()
       {
         if($("#btnControl").val() == 'start')
         {
           $("#btnControl").val("stop");
           MQTTSend("start");
         }
         else
         {
           $("#btnControl").val("start");
           MQTTSend("stop");
         }
       }

       var poly;
       // parse json data and add markers 
      function addSensorMarkers(data)
      {
        var id,lat,lng,temp,path;
        for(var index in data)
        {
           id = data[index].id;
           lat = data[index].lat;
           lng = data[index].lng;
           temp= data[index].temp;
           
           if(markers[index] != null)
            {
        	   poly = new google.maps.Polyline({
                   path:Array(markers[index].getPosition(), new google.maps.LatLng(lat,lng)),
                   strokeOpacity:0.8,
                   strokeColor:'#008CFF',
                   map:map
         		});
            
               fadeOut(poly,1000,FADE_OUT_DURATION*1000);
        	 }
           addSensorMarker(index,id,lat,lng,temp);
        }
        // create a marker cluster 
        if(markerCluster == null)
        {
           markerCluster = new MarkerClusterer(map, markers,{
            maxzoom:CLUSTER_ZOOM,
            gridSize: CLUSTER_SIZE});
        }
        else
        {
          markerCluster.clearMarkers();
          markerCluster.addMarkers(markers); 
        }
      }
      // add a new marker or update one marker
      function addSensorMarker(index,id,lat,lng,temp)
      {
        var location = new google.maps.LatLng(lat,lng); 
   
        // if markers[index] is new marker, just push in markers
        // otherwise just update markers[index] info
        if(markers[index] == null)
        {
          var marker = new google.maps.Marker({  
            id:id,
            temp:temp,
            position: location,  
            icon:'img/sensor.png'
          });
          markerClick(marker);
          markers.push(marker);
        }
        else
        {
          markers[index].setPosition(location);
          if(location == infoBox.getPosition())
       	  {
       	  	setInfoBoxContent(markers[index]);
       	  }
        }
      }
	// locate certian sensor
      function pantoSensor(id)
      {
        if(markers.length <= 0)
          return;
        for(var i = 0; i < markers.length; i++)
        {
          if(markers[i].id == id)
          {
            map.panTo(markers[i].getPosition());
            setInfoBoxContent(markers[i]);
            infoBox.open(map, markers[i]);
            break;
          }
        }
      }
	 // track fade out effect 
      function fadeOut(line, keepAround, fadeDuration)
      {
    	    setTimeout(function(){
    	        var startingOpacity = line.strokeOpacity,
    	            startTime = (new Date()).getTime();
    	                    
    	        function step(){
    	            var currentTime = (new Date()).getTime(),
    	                elapsed = currentTime - startTime,
    	                targetOpacity = startingOpacity - startingOpacity * (elapsed/fadeDuration);
    	                            
    	            line.setOptions({
    	                strokeOpacity: targetOpacity
    	            });
    	                       
    	            if(elapsed >= fadeDuration){
    	                line.setMap(null);
    	            } else {
    	                setTimeout(step, 1);
    	            }
    	        }
    	        setTimeout(step, 1);
    	    }, keepAround);
      }
	 //==================================================//
	 // MQTT
	 //==================================================//
	 var mqtt;
	 var publishTopic = "control";
	 var subscribeTopic = "sensor";
	 var reconnectTimeout = 2000;
	 // connect to MQTT server
	 function MQTTconnect() {
		    mqtt = new Paho.MQTT.Client(
		                    host,
		                    port,
		                    "calgary" + parseInt(Math.random() * 100,
		                    10));
		    var options = {
		        timeout: 3,
		        useSSL: useTLS,
		        cleanSession: cleansession,
		        onSuccess: onConnect,
		        onFailure: function (message) {
		        	console.log("Connection failed: " + message.errorMessage + "Retrying");
		            setTimeout(MQTTconnect, reconnectTimeout);
		        }
		    };
		
		    mqtt.onConnectionLost = onConnectionLost;
		    mqtt.onMessageArrived = onMessageArrived;
		
		    if (username != null) {
		        options.userName = username;
		        options.password = password;
		    }
		    console.log("Host="+ host + ", port=" + port + " TLS = " + useTLS + " username=" + username + " password=" + password);
		    mqtt.connect(options);
		}
		// connect to MQTT server successfully 
		function onConnect() 
		{
			initialize();
			console.log('Connected to ' + host + ':' + port);
		    // Connection succeeded; subscribe to our topic
			mqtt.subscribe(subscribeTopic, {qos: 0});
			console.log("subscribeTopic:"+subscribeTopic);
		     // request all sensors' information 
			MQTTSend("init");
		}
		// lost connection 
		function onConnectionLost(response) 
		{
		    setTimeout(MQTTconnect, reconnectTimeout);
		    //console.log("connection lost: " + responseObject.errorMessage + ". Reconnecting");
		};
	    // add sensosr when receiving data
		function onMessageArrived(message) 
		{
		    var topic = message.destinationName;
		    var dataobj = eval(message.payloadString);
            addSensorMarkers(dataobj);
		};
		// send control message 
		function MQTTSend(control)
		{
		    message = new Paho.MQTT.Message(control);
		    message.destinationName = publishTopic;
		    mqtt.send(message);  
		    console.log("send topic:"+publishTopic + " " + control);    
		}	
	 
    </script>
<script src="js/vendor/modernizr.js"></script>
</head>

<body onload="MQTTconnect();">
  <input id="btnControl" type="submit" value="start"
    onclick="javascript:control();" />
  <div id="map-container">
    <div id="map"></div>
  </div>


  <script src="js/vendor/jquery.js"></script>
  <script src="js/foundation.min.js"></script>
  <script>
      $(document).foundation();
    </script>
</body>
</html>