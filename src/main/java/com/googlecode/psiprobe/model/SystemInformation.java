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
package com.googlecode.psiprobe.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * POJO representing system information for "system infromation" tab.
 *
 * @author Vlad Ilyushchenko
 */
public class SystemInformation implements Serializable {

    private String appBase;
    private String configBase;
    private long maxMemory;
    private long freeMemory;
    private long totalMemory;
    private int cpuCount;
    private String serverInfo;
    private Map systemProperties = new HashMap();
    private String workingDir;

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public Date getDate() {
        return new Date();
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public String getConfigBase() {
        return configBase;
    }

    public void setConfigBase(String configBase) {
        this.configBase = configBase;
    }

    public Map getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(Map systemProperties) {
        this.systemProperties = systemProperties;
    }

    public Set getSystemPropertySet() {
        return systemProperties.entrySet();
    }
}
