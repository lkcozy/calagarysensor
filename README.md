CalgarySensor
=============

Overview
--------

CalgarySensor is a web application that stimulates 1000 sensors moving in Calgary at the speed of 150 km/hr. They will randomly change their direction of the movement and obtain observer temperature every 15 seconds. The number of sensors and parameters of the sensor, such as speed, direction change frequency and temperature observe frequency, can be modified in the configuration file.

This web application is built on the Java Spring framework and the background services should be deployed to Tomcat. 

The background services include:

 - initializeSensor.do: Creat all sensors and initialize the sensor settings.
 - stimulate.do?start=start: Start or stop stimulation, the parameter ‘start’ can be ‘start’ or ‘stop’. 
 - querySensorInfoById.do?id=sid0001: Query certain sensor information service by certain one sensor's id.
 - getSensorInfo.do:Get all sensors' information.

Screenshot
----------
![Screenshot](https://github.com/lkcozy/calgarysensor/blob/master/images/Screenshot.JPG)

DEMO
----
This web application works well both on desktop browsers and portable devices.You can see a running version of the application [here](http://159.226.111.20:8080/calgarysensormap/index.jsp) .

Quick Start
------------

 - Copy calgarysensorserver.war to webapps folder on server VM001
 - Copy calgarysensormap folder to webapps folder on server VM002
	 - Edit index.jsp
		 - Find “SERVER_HOST” and change its default value
> **Note:**

> - the default value is
> - *http://159.226.111.19:8080/calgarysensorserver/*
> - *159.226.111.19:8080* represents VM001’s IP address. 
> - *Calgarysensorserver* represents background services’ root directory
