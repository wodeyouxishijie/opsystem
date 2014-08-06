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
package com.googlecode.psiprobe.controllers.threads;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.googlecode.psiprobe.beans.ContainerListenerBean;
import com.googlecode.psiprobe.controllers.TomcatContainerController;
import com.googlecode.psiprobe.model.jmx.JmxServerInfoPuller;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerInfo;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerUtil;

/**
 * Creates the list of http connection thread pools.
 * 
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class ListThreadPoolsController extends TomcatContainerController {

    private ContainerListenerBean containerListenerBean;
    
    private JmxServerInfoPuller puller = new JmxServerInfoPuller();

    public ContainerListenerBean getContainerListenerBean() {
        return containerListenerBean;
    }

    public void setContainerListenerBean(ContainerListenerBean containerListenerBean) {
        this.containerListenerBean = containerListenerBean;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	int serverId = ServletRequestUtils.getIntParameter(request, "serverId", 0);
    	RemoteServerInfo remoteServerInfo = RemoteServerUtil.getRemoteServerInfoById(serverId);
    	List pools = null;
    	if(null == remoteServerInfo) {
	        pools = containerListenerBean.getThreadPools();
    	} else {
    		pools = puller.pullThreadInfo(remoteServerInfo);
    	}
    	
    	return new ModelAndView(getViewName())
        	.addObject("pools", pools);
    }
}
