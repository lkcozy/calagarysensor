/**
 * @Description:TODO
 * @author:lkcozy
 * @time:2014-12-27 下午5:22:50
 */
package com.nunknown.thread;

import java.util.concurrent.Callable;

/**
 * @author Kan
 *
 */
public abstract class Job implements Callable<Object>
{
    private MyLock lock = new MyLock();
    
    public Object call() throws Exception
    {
        Object result = null;
        try
        {
            result = this.execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        synchronized(lock)
        {
            lock.thread_count--;
            lock.notifyAll();
        }
        
        return result;
    }
    
    public void setLock(MyLock lock)
    {
        this.lock = lock;
    }
    
    public abstract Object execute();
}
