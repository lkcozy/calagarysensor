/**
 * @Description:TODO
 * @author:lkcozy
 * @time:2014-8-15 下午9:07:34
 */
package com.nunknown.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Utilities
 * @author Kan
 * 
 */
public final class Util
{
	public static final double EARTH_RADIUS = 6371.393;
	public static final double EQUATOR_CIRCUMFERENCE = 2 * Math.PI
			* EARTH_RADIUS * 1000;
	public static final double ONE_LATITUDE_LENGTH = EQUATOR_CIRCUMFERENCE / 360.0;

    /**
     * Calculate the distance that separates each degree of longitude on certain latitude 
     * @param latitude
     * @return
     */
    public static double Longitude_Length(double latitude)
    {
        return Util.ONE_LATITUDE_LENGTH * Math.cos(latitude * Math.PI / 180);
    }
    /**
     * Convert radians to degrees
     * @param dNum
     * @return
     */
    public static double RadiansToDegrees(double dNum)
    {
        return dNum * Math.PI / 180.0;
    }
	/**
	 * Get the number length
	 * @param sNum
	 * @return
	 */
	public static int NumberLength(int sNum)
	{
		int length = 0;
		while (sNum != 0)
		{
			sNum = sNum / 10;
			length++;
		}
		return length;
	}

	/**
	 * If value's length  is less than the require length , using zeros to pad out the value
	 * @param length
	 * @param number
	 * @return
	 */
	public static String lpad(int length, int number)
	{
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

	/**
	 *  Return current time, the format is yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurrentTime()
	{
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(d)+ Calendar.getInstance().getTimeInMillis() ;
    }
}
