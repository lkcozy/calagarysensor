package com.nunknown.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.stereotype.Controller;

import com.nunknown.service.SensorService;
import com.nunknown.util.ReadPropertiesUtil;
import com.nunknown.util.Util;

@Controller
public class MQTTController implements MqttCallback 
{
	private final static Log LOG=LogFactory.getLog(MQTTController.class);  
	
	 private static SensorService gs = SensorService.getInstance();
	 ScheduledExecutorService execService =  Executors.newScheduledThreadPool(3);;
	 
	private MqttAsyncClient client;
	private String publishTopic  = "sensor";
	private String subScribeTopic  = "control";
    private String broker       = "tcp://159.226.111.19:1883";
    private String clientId     = "calgaryserver";
    MqttMessage message = new MqttMessage();
	public MQTTController() 
	{
		publishTopic = ReadPropertiesUtil.getPropertie("pTopic");
		subScribeTopic = ReadPropertiesUtil.getPropertie("sTopic");
		broker = ReadPropertiesUtil.getPropertie("brokerHost");
		onConnect ();
	}
	
	public void onConnect () {
	    try {
	    	clientId += 10000 *  Math.random();
	    	client = new MqttAsyncClient(broker, clientId);
	        LOG.info("Connecting to broker: "+broker);
	        client.connect();
	        LOG.info("Connected");
	        client.setCallback(this);
	        if(client.isConnected())
	        {
	        	client.subscribe(subScribeTopic,0);
	        }
	        
	    } catch (MqttException e) {
	        e.printStackTrace();
	        LOG.error(e.toString());
	    }
	}
	
	public  void sendMessage(String content) 
	{
		message = new MqttMessage(content.getBytes());
        try
		{
		  client.publish(publishTopic, message);
		  LOG.info("Message published");
		} catch (MqttPersistenceException e)
		{
			LOG.error(e.toString());
		} catch (MqttException e)
		{
			LOG.error(e.toString());
		}
	}
	@Override
	public void connectionLost(Throwable cause) {
	    // TODO Auto-generated method stub
		LOG.info("connectionLost");
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message)
	        throws Exception 
	  {
		String msg = message.toString();
		LOG.info(msg);
		if(msg.equals("init"))
		{
			if(initializeSensor())
			{
				sendMessage(gs.toJSON());
			}
		}
		else if(msg.equals("start"))
		{
			stimulate("start");
		}
		else if(msg.equals("stop"))
		{
			stimulate("stop");
		}
		else
		{
			querySensorInfo(msg);
		}
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
	    // TODO Auto-generated method stub
	}
	
	 public boolean initializeSensor() 
	 {
		 boolean result = true;
		 try
		 {
	    	int sNum = Integer.parseInt(ReadPropertiesUtil.getPropertie("sNum"));
	    	SensorService.getInstance().CreateSensor(1, sNum, Util.NumberLength(sNum));
	    	LOG.info("Initalize sensors");
		 }
		 catch(Exception e)
		 {
			 LOG.error(e.toString());
			 result = false;
		 }
	        return result;
	    }
	    
	    public String stimulate(String start) 
	    { 
	    	LOG.info("Initalize sensors");
	    	String status = "success";
	    	if(gs.getSensorNum() == 0) initializeSensor();
	    	if(start.equals("start"))
	    	{
	    		try
	    		{
		    		final int refreshRate = Integer.parseInt(ReadPropertiesUtil.getPropertie("cdRate"));
		    		final float stimulateRate = Float.parseFloat(ReadPropertiesUtil.getPropertie("sRate"));
		    		final int readTempRateint = Integer.parseInt(ReadPropertiesUtil.getPropertie("rtRate"));
		    		 execService =   Executors.newScheduledThreadPool(3);
		             execService.scheduleAtFixedRate(new Runnable()
		             {
		                 public void run()
		                 {
		                    gs.StimulateAllSensor(refreshRate,readTempRateint,stimulateRate);
		                    sendMessage(gs.toJSON());
		                 }
		             }, 0, (long) (stimulateRate * 1000), TimeUnit.MILLISECONDS);
	    		}catch(Exception e)
	    		{
	    			status = e.toString();
	    			LOG.error("Stimulate:"+e.toString());
	    		}
	    	}
	    	else if(start.equals("stop"))
	    	{
	    		execService.shutdown();
	    		LOG.info("Stimulate: shutdown ScheduledEx	ecutorService.");
	    	}
	    	else
	    	{
	    	    status="Parameter error";
	    	}
	         return status;
	    }
	    
	    public String querySensorInfo(String id) 
	    {
	    	if(gs == null) initializeSensor();
	    	return gs.SensorInfo(id);
	    }
}