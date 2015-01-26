package com.nunknown.model;
/**
 * Store sensor's information 
 * @author LK
 */
public class Sensor
{
    // sensor's identification
	private String id;
	//sensor's speed, default is 150km/h
	private double speed;
	 //the moving direction 
	private int direction;
	// the latest observed temperature 
	private float temperature;
	// the latest  position 
	private LatLng position;

	public Sensor()
	{
	}

	public Sensor(String id, double speed, int direction, float temperature, LatLng position)
	{
		this.id = id;
		this.speed = speed;
		this.direction = direction;
		this.temperature = temperature;
		this.position = position;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public double getSpeed()
	{
		return speed;
	}
	
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	public float getTemperature()
	{
		return temperature;
	}

	public void setTemperature(float temperature)
	{
		this.temperature = temperature;
	}

	public int getDirection()
	{
		return direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	public LatLng getPosition()
	{
		return position;
	}

	public void setPosition(LatLng position)
	{
		this.position = position;
	}
	/**
	 * Get sensor's latest information, include id, position and observed temperature
	 */
	public String toString()
	{
		return "Sensor id:" + id + "  Position:(" + position.getLatitude()
				+ "," + position.getLongitude() + ") Temp:" + temperature + " Celsius";
	}
}
