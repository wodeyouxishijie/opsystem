package com.googlecode.psiprobe.tools.jmxserver;

/**
 * Remote Server Info
 * @author Jacky
 */
public class RemoteServerInfo {
	
	private String serverName;
	
	private String version;
	
	private String remoteIp;
	
	private int jmxPort;
	
	private String jmxURIPath;
	
	private String httpProtocal;
	
	private int httpPort = 80;
	
	private String httpURIPath;
	
	private String keyWords;
	
	// 1tomcat?2node?3jboss?
	private int serverType;
	
	public RemoteServerInfo(String serverName,String version,String remoteIp,int jmxPort,
			String jmxURIPath,String httpProtocal,int httpPort,String httpURIPath,int serverType,String keyWords) {
		this.serverName = serverName;
		this.version = version;
		this.remoteIp = remoteIp;
		this.jmxPort = jmxPort;
		this.jmxURIPath = jmxURIPath;
		this.httpProtocal = httpProtocal;
		this.httpPort = httpPort;
		this.httpURIPath = httpURIPath;
		this.serverType = serverType;
		this.keyWords = keyWords;
	}

	public String getJmxUrl() {
		return "service:jmx:rmi:///jndi/rmi://"+remoteIp+":"+jmxPort+"/"+jmxURIPath;
	}
	
	public String getHttpTestUrl() {
		return httpProtocal+"://"+remoteIp+":"+httpPort+"/"+httpURIPath;
	}
	
	public String getJmxURIPath() {
		return jmxURIPath;
	}

	public void setJmxURIPath(String jmxURIPath) {
		this.jmxURIPath = jmxURIPath;
	}

	public String getHttpProtocal() {
		return httpProtocal;
	}

	public void setHttpProtocal(String httpProtocal) {
		this.httpProtocal = httpProtocal;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public String getHttpURIPath() {
		return httpURIPath;
	}

	public void setHttpURIPath(String httpURIPath) {
		this.httpURIPath = httpURIPath;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}
	
}
