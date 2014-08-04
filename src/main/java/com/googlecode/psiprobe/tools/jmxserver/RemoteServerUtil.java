package com.googlecode.psiprobe.tools.jmxserver;

import java.util.HashMap;
import java.util.Map;

public class RemoteServerUtil {
	
	private static Map<Integer,RemoteServerInfo> remoteServerMap = new HashMap<Integer,RemoteServerInfo>(){
		private static final long serialVersionUID = 2292514941733707046L;
        {
		this.put(1, new RemoteServerInfo("Tomcat Server1","7.0","127.0.0.1",10004,"jmxrmi",80,"stats.jsp",1));
	}};
	
	public static RemoteServerInfo getRemoteServerInfoById(int serverId) {
		return remoteServerMap.get(serverId);
	}
	
}
