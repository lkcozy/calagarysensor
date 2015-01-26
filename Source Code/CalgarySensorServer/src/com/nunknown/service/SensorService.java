package com.nunknown.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nunknown.model.LatLng;
import com.nunknown.model.Sensor;
import com.nunknown.util.ReadPropertiesUtil;
import com.nunknown.util.SensorConst;
import com.nunknown.util.Util;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


/**
 * 
 * @author kan
 *
 */
public class SensorService
{
	private final static Log LOG=LogFactory.getLog(SensorService.class);  
	// static factory method
	private static final SensorService instance = new SensorService();
	// random number generator
    private static Random r = new Random();
    // store all sensors
    private static List<Sensor> sensorList = new ArrayList<Sensor>();
    // longitude span
    private double lngSpan;
    // latitude span
    private double latSpan;
    // set temperature range
	private static int minTemp = -30;
	private static int maxTemp = 30;
	// all sensor's initial speed
	private static double speed = 150;
	// store  sensor's moving time in one direction 
	private int [] rRate;
	// store sensor's the next time of  reading observed temperature
	private int tRate = 0;
	// boundary of Calgary
	private Geometry boundary ;
    // wkt string reader
	private WKTReader wkt = new WKTReader(); 
	 
	private   SensorService()
	{
	     lngSpan = SensorConst.northEast.getLongitude() - SensorConst.southWest.getLongitude();
         latSpan = SensorConst.northEast.getLatitude() - SensorConst.southWest.getLatitude();
 		minTemp = Integer.parseInt(ReadPropertiesUtil.getPropertie("minTemp"));
 		maxTemp = Integer.parseInt(ReadPropertiesUtil.getPropertie("maxTemp"));
 		speed = Double.parseDouble(ReadPropertiesUtil.getPropertie("speed"));
 		InitBoudnary();
	}
	
	public static SensorService getInstance()
	{
		return instance;
	}
	/**
	 *  Initialize boundary of Calgary coordinate arrays
	 * @exception:
	 */
	private void InitBoudnary()
    {
        String str = "POLYGON (("; 
        for(LatLng tmp : SensorConst.CALGARY_BOUNDARY)
        {
            str = str + tmp.getLongitude() + " "+tmp.getLatitude() + ",";
        }
        str =  str.substring(0, str.length()-1);
        str += "))";
        try
        {
            boundary = wkt.read(str);
        }
        catch (ParseException e)
        {
            LOG.error(e.toString());
        }
    }
	
	/**
	 * Initialize all sensors information
	 * @param sIndex
	 * @param eIndex
	 * @param length
	 */
	public  void CreateSensor(int sIndex,int eIndex,int length)
	{
		RemoveAllSensor();
		
		if(eIndex < sIndex || sIndex < 0 || eIndex < 0)
			return;
		
		 rRate = new int[eIndex-sIndex+1];
		 
		 Sensor tmpSensor = new Sensor();
		 LatLng position;
		 // create sensors
		 for(int i =sIndex; i <= eIndex;i++)
		 {
			 // randomly create sensor's position in calgary
            do
             {
                position =  new LatLng(
                        SensorConst.southWest.getLatitude() + latSpan * Math.random(),
                        SensorConst.southWest.getLongitude() + lngSpan * Math.random());
             }while(!BoundaryDection(position));
            
			 tmpSensor = new Sensor("sid"+Util.lpad(length,i), 
					 speed, 
					 StimulateChangeDirection(), 
					 StimulateReadTemp(minTemp,maxTemp),
					 position);
			 
			     sensorList.add(tmpSensor);
			     rRate[i-1] = 0;
		 }
		 LOG.info("Create :"+(eIndex-sIndex+1));
	}
	/**
	 * Clear all exist sensors
	 */
	private void RemoveAllSensor()
	{
		for(int i = sensorList.size()-1; i >= 0; i--)
		{
			sensorList.remove(i);
		}
		LOG.info("RemoveAllSensor");
	}
	/**
	 * 
	 * Stimulate moving and reading observed temperature of all sensors
	 * @param refreshRate
	 * @param readTempRate
	 * @param stimulateRate
	 * @exception:
	 */
	public  void StimulateAllSensor(int refreshRate,int readTempRate, float stimulateRate)
	{
	    // stimulate once every stimulateRate 
		 for(int i =0; i < sensorList.size();i++)
		 {
			 StimulatePostion(sensorList.get(i), i, refreshRate, readTempRate,stimulateRate);
		 }
	}
	 /**
     * 
     * Stimulate moving and reading observed temperature from sensorList[s] to sensorList[e]
     * @param refreshRate
     * @param readTempRate
     * @param stimulateRate
     * @exception:
     */
   public  void StimulateAllSensor(int s,int e,int refreshRate,int readTempRate, int stimulateRate)
    {
       // stimulate once every stimulateRate 
         for(int i =s-1; i < e;i++)
         {
             StimulatePostion(sensorList.get(i), i, refreshRate, readTempRate,stimulateRate);
         }
    }
	/**
	 *Query sensor information through id
	 * @param id
	 * @return sensor's information in json format
	 */
	public  String SensorInfo(String id)
	{
		if(id.length() == 0)
			return "Parameter error, id should be like sid0001";
		Sensor temp = new Sensor();
		StringBuilder sb=new StringBuilder();  
		sb.append("[{");
		for (int i = 0; i < sensorList.size();i++)
		{
			temp = sensorList.get(i);
			if(temp.getId().equals(id))
			{
				try
				 {
					 sb.append("{\"id\":");
					 sb.append("\""+temp.getId()+"\",");
					 sb.append("\"lat\":");
					 sb.append("\""+temp.getPosition().getLatitude()+"\",");
					 sb.append("\"lng\":");
					 sb.append("\""+temp.getPosition().getLongitude()+"\",");
					 sb.append("\"temp\":");
					 sb.append("\""+temp.getTemperature()+"\"}");
				 }
				 catch(Exception e)
				 {
					 LOG.error("SensorInfo:"+e.toString());
				 }
				return sb.toString();
			}
		}
		return "Not found";
	}

	/**
	 * This converts sensor's information to  json format
	 * @return all sensors' information in json format 
	 */
	public String toJSON()
	{
		StringBuilder sb=new StringBuilder();  
		 sb.append("[");
		 for(int i =0; i < sensorList.size();i++)
		 {
			 try
			 {
				 sb.append("{\"id\":");
				 sb.append("\""+sensorList.get(i).getId()+"\",");
				 sb.append("\"lat\":");
				 sb.append("\""+sensorList.get(i).getPosition().getLatitude()+"\",");
				 sb.append("\"lng\":");
				 sb.append("\""+sensorList.get(i).getPosition().getLongitude()+"\",");
				 sb.append("\"temp\":");
				 sb.append("\""+sensorList.get(i).getTemperature()+"\"},");
			 }
			 catch(Exception e)
			 {
				 LOG.error("toJSON:"+e.toString());
			 }
		 }
		 sb.setLength(sb.length()-1);
		 sb.append("]");
		 LOG.debug("sensor size:"+sensorList.size());
		 return sb.toString();
	}
	/**
	 * Calculate sensor's new position
	 * @param sensor
	 * @param index
	 * @param refreshRate
	 * @param readTempRate
	 * @param stimulateRate
	 * @exception:
	 */
	public void StimulatePostion(Sensor sensor,int index, int refreshRate, int readTempRate,float stimulateRate)
	{
	    if(rRate[index] >= refreshRate)
	    {
	        sensor.setDirection(StimulateChangeDirection());
	        rRate[index] = 0;
	    }
	    
	    if(tRate >= readTempRate )
	    {
	    	 sensor.setTemperature(StimulateReadTemp());
	    	 tRate = 0;
	    }
	    
	    LatLng newPostion = NewPosition(sensor,stimulateRate);
	    // To determine whether the new position  is in Calgary
	    if(!BoundaryDection(newPostion))
	    {
	    	LOG.debug("Sensor ID:"+sensor.getId()+" Out of Calgary");
	    	TurnAroundSensorDirection(sensor);
	        newPostion = NewPosition(sensor,1);
	        // reset 
	        rRate[index] = 0;
	    }
	    sensor.setPosition(newPostion);
	    LOG.debug("new position:"+newPostion);
	    rRate[index] += stimulateRate;
	    tRate += stimulateRate;
	}
	
	/**
	 * Calculate new position after sensor moving for a period of time
	 * @param sensor
	 * @param seconds
	 * @return
	 * @exception:
	 */
	private LatLng NewPosition(Sensor sensor, float seconds)
	{
	    LatLng dPosition = DeltaPosition(sensor,seconds);
        return new LatLng(sensor.getPosition().getLatitude() + dPosition.getLatitude(),
        		sensor.getPosition().getLongitude() + dPosition.getLongitude());
	}
    
	/**
	 * Stimulate sensor reads observed temperature 
	 * @exception:
	 */
	public float  StimulateReadTemp()
	{
	     return StimulateReadTemp(minTemp, maxTemp);
	}
	/**
	 * Randomly set sensor's observed temperature, 
	 * the degree is accurate to one decimal place
	 * @param min
	 * @param max
	 */
	private float StimulateReadTemp(float min, float max)
	{
     return  (float) (Math.ceil((min + (max - min) * Math.random()) * 10) / 10.0);
	}
	
	/**
	 * Calculate the longitude and latitude changes of a sensor in certain time.
	 * @param direction
	 * @param seconds
	 * @return variation position
	 */
	public  LatLng DeltaPosition(Sensor sensor, float seconds)
	{
		double deltaLng, deltaLat;
		deltaLng = sensor.getSpeed()* seconds * Math.sin(Util.RadiansToDegrees(sensor.getDirection()))
				/ (Util.ONE_LATITUDE_LENGTH * Math.cos(Util.RadiansToDegrees(sensor.getPosition().getLatitude())));
		deltaLat =sensor.getSpeed() * seconds * Math.cos(Util.RadiansToDegrees(sensor.getDirection()))
				/ Util.ONE_LATITUDE_LENGTH;

		return new LatLng(deltaLat,deltaLng);
	}

	/**
	 * Randomly set sensor's direction  from 0~359
	 * @return new direction
	 * @exception:
	 */
	public  int StimulateChangeDirection()
	{
		return r.nextInt(359) ;
	}
	/**
	 * When sensor will move out of Calgary, turn around its direction.
	 * @param sensor
	 * @exception:
	 */
	public  void TurnAroundSensorDirection(Sensor sensor)
	{
		int direction = sensor.getDirection();
		LOG.debug("last direction "+ direction);
		direction = (direction <= 180 && direction > 0) ? direction + 180 : direction -180;
		sensor.setDirection(direction);
		LOG.debug("Change direction "+direction);
	}
	
	/**
     * To determine whether a new position is in the Calgary 
     * Use rectangular boundary detection
     * @param postion
     * @return  true or false
     */
    private boolean BoundaryDection(LatLng position)
    {
        Geometry sensor = null;
        try
        {
            sensor = wkt.read("POINT(" + position.getLongitude() + " " + position.getLatitude() + "))");
        }
        catch (ParseException e)
        {
           LOG.error(e.toString());
        } 
        return boundary.contains(sensor);
    }
    /**
     * Get existing sensor number 
     * @return sensor number
     * @exception:
     */
    public int getSensorNum()
    {
        return sensorList.size();
    }
}
