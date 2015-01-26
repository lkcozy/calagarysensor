package com.nunknown.controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 
 * @author Kan
 * @description Base Controller
 */
public class BaseController 
{
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception 
    {
        
    }
}
