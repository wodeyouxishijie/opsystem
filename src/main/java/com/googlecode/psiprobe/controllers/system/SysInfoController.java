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
package com.googlecode.psiprobe.controllers.system;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.util.ServerInfo;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.googlecode.psiprobe.beans.RuntimeInfoAccessorBean;
import com.googlecode.psiprobe.controllers.TomcatContainerController;
import com.googlecode.psiprobe.model.SystemInformation;
import com.googlecode.psiprobe.model.jmx.JmxServerInfoPuller;
import com.googlecode.psiprobe.model.jmx.RuntimeInformation;
import com.googlecode.psiprobe.tools.SecurityUtils;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerInfo;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerUtil;

/**
 * Creates an instance of SystemInformation POJO.
 * 
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class SysInfoController extends TomcatContainerController {

    private List filterOutKeys = new ArrayList();
    private RuntimeInfoAccessorBean runtimeInfoAccessor;
    private long collectionPeriod;
    private JmxServerInfoPuller puller = new JmxServerInfoPuller();

    public List getFilterOutKeys() {
        return filterOutKeys;
    }

    public void setFilterOutKeys(List filterOutKeys) {
        this.filterOutKeys = filterOutKeys;
    }

    public RuntimeInfoAccessorBean getRuntimeInfoAccessor() {
        return runtimeInfoAccessor;
    }

    public void setRuntimeInfoAccessor(RuntimeInfoAccessorBean runtimeInfoAccessor) {
        this.runtimeInfoAccessor = runtimeInfoAccessor;
    }

    public long getCollectionPeriod() {
        return collectionPeriod;
    }

    public void setCollectionPeriod(long collectionPeriod) {
        this.collectionPeriod = collectionPeriod;
    }

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SystemInformation systemInformation = null;
        int serverId = ServletRequestUtils.getIntParameter(request, "serverId", 0);
        RemoteServerInfo remoteServerInfo = RemoteServerUtil.getRemoteServerInfoById(serverId);
        Long collectPeriod = 0L;
        RuntimeInformation runtimeInformation = null;
        if(null ==  remoteServerInfo) {
        	systemInformation = new SystemInformation();
	        systemInformation.setAppBase(getContainerWrapper().getTomcatContainer().getAppBase().getAbsolutePath());
	        systemInformation.setConfigBase(getContainerWrapper().getTomcatContainer().getConfigBase());
	        systemInformation.setCpuCount(Runtime.getRuntime().availableProcessors());
	        systemInformation.setFreeMemory(Runtime.getRuntime().freeMemory());
	        systemInformation.setMaxMemory(Runtime.getRuntime().maxMemory());
	        systemInformation.setServerInfo(ServerInfo.getServerInfo());
	        systemInformation.setTotalMemory(Runtime.getRuntime().totalMemory());
	        systemInformation.setWorkingDir(new File("").getAbsolutePath());
	        Map sysProps = new Properties();
	        sysProps.putAll(System.getProperties());
	        if (!SecurityUtils.hasAttributeValueRole(getServletContext(), request)) {
	            for (Iterator it = filterOutKeys.iterator(); it.hasNext();) {
	                sysProps.remove(it.next());
	            }
	        }
	        systemInformation.setSystemProperties(sysProps);
	        collectPeriod = new Long(getCollectionPeriod());
	        runtimeInformation = getRuntimeInfoAccessor().getRuntimeInformation();
        } else {
        	systemInformation = puller.pullSystemInformation(remoteServerInfo);
        	
        }

        return new ModelAndView(getViewName())
                .addObject("systemInformation", systemInformation)
                .addObject("runtime", runtimeInformation)
                .addObject("collectionPeriod", collectPeriod);
    }
}
