/**
 * @Description:TODO
 * @author:lkcozy
 * @time:2014-12-27 下午4:34:28
 */
package com.nunknown.thread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nunknown.service.SensorService;

/**
 * Simplified concurrency framework
 * @author kan
 *
 */
public class Executer
{
    private List<Future<Object>> futures = new ArrayList<Future<Object>>();   
    public static final MyLock lock = new MyLock ();
    private ExecutorService  pool = null;
    public Executer()
    {
        this(1);
    }
    
    public Executer(int threadPoolSize)
    {
        pool = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    public void distributejob(Job job)
    {
        job.setLock(lock);
        futures.add(pool.submit(job));
        synchronized(lock)
        {
            lock.thread_count++;
        }
    }
    
    public void Shutdown()
    {
        this.pool.shutdown();
    }
    
    /**
     *  Processing results of each task 
     * @return
     * @exception:
     */
    public List<Object> join()
    {
        synchronized(lock)
        {
            while(lock.thread_count > 0)
            {
                try
                {
                    lock.wait();
                }
                catch(InterruptedException  e)
                {
                    e.printStackTrace();  
                }
            }
        }
        List<Object> list = new ArrayList<Object>();
       
        for (Future<Object> future : futures) {
            try {
                Object result = future.get();
                if(result != null){
                    if(result instanceof List)
                        list.addAll((List)result);
                    else
                        list.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return list;
    }
    
    public static void main(String[] args) 
    {
       SensorService gs = SensorService.getInstance();
       int totalNum = 1000000;
       gs.CreateSensor(1, totalNum, 6);
       Executer exe = new Executer(5);
       int jobNum =5;
       int sIndex = 1;
       int span = totalNum / jobNum;
       int eIndex = span;
       for(int i = 0; i < jobNum; i++)
       {
           StimulateJob job = new StimulateJob(sIndex,eIndex);
           exe.distributejob(job);
           sIndex = eIndex + 1;
           eIndex += span; 
       }
           exe.Shutdown();
    }
}
