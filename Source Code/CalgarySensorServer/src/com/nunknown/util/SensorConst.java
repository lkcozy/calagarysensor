/**
 * @Description:TODO
 * @author:lkcozy
 * @time:2014-12-27 下午2:29:32
 */
package com.nunknown.util;

import com.nunknown.model.LatLng;

/**
 * Constant class
 * @author Kan
 *
 */
public interface SensorConst
{
    // Boundary of Calgary
    public static LatLng[] CALGARY_BOUNDARY =
        new LatLng[]{new LatLng(51.183012, -113.913481),
                                new LatLng(50.890267, -113.913481),
                                new LatLng(50.905631,-114.170286),
                                new LatLng(51.007933,-114.170286),
                                new LatLng(51.007933,-114.234831),
                                new LatLng(51.183012,-114.234831),
                                new LatLng(51.183012, -113.913481)
        };
    public static LatLng northEast= new LatLng(51.183012, -113.913481);
    public static LatLng southWest = new LatLng(50.905631,-114.2262268);
}