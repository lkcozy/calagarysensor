package com.nunknown.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nunknown.service.SensorService;
import com.nunknown.util.ReadPropertiesUtil;
import com.nunknown.util.Util;


@Controller
public class SensorController extends BaseController
{
	private final static Log LOG=LogFactory.getLog(SensorController.class);  
	
    private static SensorService gs = SensorService.getInstance();
    ScheduledExecutorService execService =  Executors.newScheduledThreadPool(3);
    
    @ResponseBody
    @RequestMapping("/initializeSensor.do")
    public String initializeSensor() 
    {
    	int sNum = Integer.parseInt(ReadPropertiesUtil.getPropertie("sNum"));
    	SensorService.getInstance().CreateSensor(1, sNum, Util.NumberLength(sNum));
    	LOG.info("Initalize sensors");
        return"success";
    }
    
    @ResponseBody
    @RequestMapping("/stimulate.do")
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
    
    @ResponseBody
    @RequestMapping("/querySensorInfo.do")
    public String querySensorInfo(String id) 
    {
    	if(gs == null) initializeSensor();
    	return gs.SensorInfo(id);
    }
    
    @ResponseBody
    @RequestMapping("/getSensorInfo.do")
    public String getSensorInfo() 
    {
    	if(gs == null) initializeSensor();
    	return gs.toJSON();
    }
}
