package com.googlecode.psiprobe.tools.jmxserver;

import java.util.Map;

public class RemoteServerUtil {
	
	private static Map<Integer,RemoteServerInfo> remoteServerMap = null;
	
	public static RemoteServerInfo getRemoteServerInfoById(int serverId) {
		return remoteServerMap.get(serverId);
	}

	public static Map<Integer, RemoteServerInfo> getRemoteServerMap() {
		return remoteServerMap;
	}

	public void setRemoteServerMap(
			Map<Integer, RemoteServerInfo> remoteServerMap) {
		RemoteServerUtil.remoteServerMap = remoteServerMap;
	}
	
}
