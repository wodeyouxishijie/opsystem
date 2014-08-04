/*
 * Licensed under the GPL License.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.googlecode.psiprobe.controllers.apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.googlecode.psiprobe.controllers.TomcatContainerController;
import com.googlecode.psiprobe.model.Application;
import com.googlecode.psiprobe.tools.ApplicationUtils;
import com.googlecode.psiprobe.tools.SecurityUtils;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerInfo;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerUtil;

/**
 * Creates the list of web application installed in the same "host" as the
 * Probe.
 * 
 * @author Vlad Ilyushchenko
 * @author Andy Shapoval
 * @author Mark Lewis
 */
public class ListWebappsController extends TomcatContainerController {
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean calcSize = ServletRequestUtils.getBooleanParameter(request, "size", false)
                && SecurityUtils.hasAttributeValueRole(getServletContext(), request);
        
        List apps;
        
        int serverId = ServletRequestUtils.getIntParameter(request, "serverId", 0);
        RemoteServerInfo remoteServerInfo = RemoteServerUtil.getRemoteServerInfoById(serverId);
        if(null == remoteServerInfo) {
	        try {
	            apps = getContainerWrapper().getTomcatContainer().findContexts();
	        } catch (NullPointerException ex) {
	            throw new IllegalStateException("No container found for your server: " + getServletContext().getServerInfo(), ex);
	        }
	        List<Application> applications = new ArrayList<Application>(apps.size());
	        boolean showResources = getContainerWrapper().getResourceResolver().supportsPrivateResources();
	        for (int i = 0; i < apps.size(); i++) {
	            Context appContext = (Context) apps.get(i);
	            if (appContext.getName() != null) {
	                applications.add(ApplicationUtils.getApplication(appContext, getContainerWrapper().getResourceResolver(), calcSize));
	            }
	        }
	        if (! applications.isEmpty() && ! showResources) {
	            request.setAttribute("no_resources", Boolean.TRUE);
	        }
	        return new ModelAndView(getViewName(), "apps", applications);
        } else {
        	JMXServiceURL serviceURL = new JMXServiceURL(remoteServerInfo.getJmxUrl()); 
        	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
        	MBeanServerConnection connection = connector.getMBeanServerConnection();  
        	
        	return new ModelAndView(getViewName(), "apps", null);
        }
    }
    
    public static void main(String[] args) throws Exception {
    	String jmxURL = RemoteServerUtil.getRemoteServerInfoById(1).getJmxUrl();
    	System.out.println(jmxURL);
    	JMXServiceURL serviceURL = new JMXServiceURL(jmxURL); 
    	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    	MBeanServerConnection connection = connector.getMBeanServerConnection();  
    	ObjectName threadObjName = new ObjectName("Catalina:type=Host,host=localhost");  
    	Object obj = connection.getAttribute(threadObjName, "children");
    	System.out.println(obj);
    	ObjectName children = new ObjectName("Catalina:j2eeType=WebModule,name=//localhost/probe,J2EEApplication=none,J2EEServer=none");
    	AttributeList attriList = connection.getAttributes(children, new String[]{"displayName","crossContext"});
    	Iterator iter = attriList.iterator();
    	Map<String,Object> attMap = new HashMap<String,Object>();
    	while(iter.hasNext()) {
    		Attribute objs = (Attribute)iter.next();
    		attMap.put(objs.getName(), objs.getValue());
    	}
        //MBeanInfo mbInfo = connection.getMBeanInfo(threadObjName);  
	}
}
