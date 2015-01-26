package com.nunknown.util;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Read properties file
 * @author Kan
 * 
 */
public final class ReadPropertiesUtil
{
	 private final static Log LOG=LogFactory.getLog(ReadPropertiesUtil.class);  
	 
    private static Properties prop = new Properties();
    static
    {
        prop = ClassLoaderUtil.getProperties("module.properties");
    }

    private ReadPropertiesUtil()
    {
        LOG.debug("ReadPropertiesUtil");
    }
    // return key value
    public static String getPropertie(String key)
    {
        return prop.getProperty(key).trim();
    }
}