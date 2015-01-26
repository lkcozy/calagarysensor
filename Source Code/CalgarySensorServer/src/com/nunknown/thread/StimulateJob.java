/**
 * @Description:TODO
 * @author:lkcozy
 * @time:2014-12-27 下午5:27:05
 */
package com.nunknown.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nunknown.service.SensorService;

/**
 * Stimulating task
 * @author Kan
 *
 */
public class StimulateJob extends Job
{
    private final static Log LOG=LogFactory.getLog(StimulateJob.class);  
    private static SensorService gs = SensorService.getInstance();
    private int startIndex;
    private int endIndex;
    
    @Override
    public Object execute()
    {
        long time = System.currentTimeMillis();
        LOG.debug( "Stimulate sensor from "+ startIndex + " to " + endIndex +" start");
        gs.StimulateAllSensor(this.startIndex,this.endIndex,15, 15, 1);
        LOG.debug((System.currentTimeMillis()-time) + " Stimulate sensor from "+ startIndex + " to " + endIndex +" end");
        return Thread.currentThread().getId();
    }
    /**
     * Set the id range of the sensor
     * @param s
     * @param e
     */
    public  StimulateJob(int s, int e)
    {
        this.startIndex = s;
        this.endIndex = e;
    }
}
