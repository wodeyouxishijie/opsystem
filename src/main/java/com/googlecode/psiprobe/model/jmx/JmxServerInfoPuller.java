package com.googlecode.psiprobe.model.jmx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.VersionInfo;

import com.googlecode.psiprobe.model.Application;
import com.googlecode.psiprobe.model.SunThread;
import com.googlecode.psiprobe.model.SystemInformation;
import com.googlecode.psiprobe.model.ThreadPool;
import com.googlecode.psiprobe.model.ThreadStackElement;
import com.googlecode.psiprobe.tools.JmxTools;
import com.googlecode.psiprobe.tools.jmxserver.RemoteServerInfo;

public class JmxServerInfoPuller {
	
	public ClientConnectionManager connectManager = new ThreadSafeClientConnManager();
	
	public static final String BASER_SERVIER_INFO = "Catalina:type=Host,host=localhost";
	
	public static final String SESSION_INFO = "Catalina:type=Manager,*";
	
	public static final String THREAD_INFO = "Catalina:type=ThreadPool,*";
	
	public static final String THREAD_LIST_INFO = "java.lang:type=Threading";
	
	public static final String MEMORY_INFO = "java.lang:type=Memory";
	
	public static final String RUNTIME_INFO = "java.lang:type=Runtime";
	
	public static final String SYSTEM_INFO = "java.lang:type=OperatingSystem";
	
	public static final String SERVER_MODULE = "children";
	
	public static final String[] SHOW_PARAM = new String[]{"displayName",
		"name","requestCount","sessionTimeout","distributable"};
	
	HttpClient client = null;
	
	{
		HttpParams param  = new BasicHttpParams();
		HttpProtocolParams.setVersion(param, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(param, HTTP.UTF_8);
        HttpConnectionParams.setTcpNoDelay(param, true);
        HttpConnectionParams.setSocketBufferSize(param, 8192);
        HttpConnectionParams.setConnectionTimeout(param, 2000);
        HttpConnectionParams.setSoTimeout(param, 2000);
        VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", DefaultHttpClient.class.getClassLoader());
        String release = (vi != null) ?vi.getRelease() : VersionInfo.UNAVAILABLE;
        HttpProtocolParams.setUserAgent(param,"Apache-HttpClient/" + release + " (java 1.5)");
        client = new DefaultHttpClient(connectManager,param);
	}
	
	public List<Application> pullApplicationInfo(RemoteServerInfo remoteServerInfo) throws IOException, 
			MalformedObjectNameException, NullPointerException, 
			InstanceNotFoundException, ReflectionException, 
			AttributeNotFoundException, MBeanException {
		JMXServiceURL serviceURL = new JMXServiceURL(remoteServerInfo.getJmxUrl()); 
    	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    	MBeanServerConnection connection = connector.getMBeanServerConnection();  
    	List<Application> apps = new ArrayList<Application>();
    	/** =======================web module信息============================= **/
    	ObjectName webModule = new ObjectName(BASER_SERVIER_INFO);  
    	ObjectName[] obj = (ObjectName[])connection.getAttribute(webModule, SERVER_MODULE);
    	
    	/** =======================web server session============================= **/
    	ObjectName managerObjName = new ObjectName(SESSION_INFO);
    	@SuppressWarnings("unchecked")
		Set<ObjectName> sessionSet = connection.queryNames(managerObjName, null); 
    	Map<String,ObjectName> sessionMap = new HashMap<String,ObjectName>();
    	for(ObjectName objName : sessionSet) {
    		sessionMap.put(objName.getKeyProperty("path"), new ObjectName(objName.getCanonicalName()));
    	}
    	
    	for(ObjectName obn : obj) {
    		
    		ObjectName children = new ObjectName(obn.toString());
    		
        	AttributeList attriList = connection.getAttributes(children, SHOW_PARAM);
        	
        	Application app = new Application();
        	
        	// session信息
        	putSessionInfo(app,sessionMap,connection);
        	
        	// 基本信息
        	putBasicInfo(app,putAttributeToMap(attriList));
        	
        	/** =======================web server http请求状态============================= **/
        	isServerAlive(app,remoteServerInfo);
        	
        	apps.add(app);
    	}
    	return apps;
	}
	
	public void putSessionInfo(Application app, Map<String,ObjectName> sessionMap,MBeanServerConnection connection) {
		ObjectName objName = sessionMap.get(app.getName());
		if(null != objName) {
			try {
				Object obj = connection.getAttribute(objName, "activeSessions");
				if(null != obj && NumberUtils.isNumber(obj.toString())) {
					app.setSessionCount((Integer)obj);;
				}
			} catch (Exception e) {
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List pullThreadInfo(RemoteServerInfo remoteServerInfo) throws Exception {
		JMXServiceURL serviceURL = new JMXServiceURL(remoteServerInfo.getJmxUrl()); 
    	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    	MBeanServerConnection connection = connector.getMBeanServerConnection();  
    	List threadList = new ArrayList();
    	ObjectName objName = new ObjectName(THREAD_INFO);
    	Set<ObjectName> objSet = connection.queryNames(objName, null);
    	for(ObjectName objN : objSet) {
    		ThreadPool threadPool = new ThreadPool();
    		threadPool.setName(objN.getKeyProperty("name"));
    		ObjectName threadInfoObj=new ObjectName(objN.getCanonicalName());
    		threadPool.setMaxThreads((Integer)connection.getAttribute(threadInfoObj, "maxThreads"));
            threadPool.setMaxSpareThreads(0);
            threadPool.setMinSpareThreads((Integer)connection.getAttribute(threadInfoObj,  "minSpareThreads"));
            threadPool.setCurrentThreadsBusy((Integer)connection.getAttribute(threadInfoObj,  "currentThreadsBusy"));
            threadPool.setCurrentThreadCount((Integer)connection.getAttribute(threadInfoObj,  "currentThreadCount"));
            threadList.add(threadPool);
    	}
		return threadList;
	}
	
	public SystemInformation pullSystemInformation(RemoteServerInfo remoteServerInfo) throws Exception {
		SystemInformation systemInformation = new SystemInformation();
		JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:10004/jmxrmi"); 
    	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    	MBeanServerConnection connection = connector.getMBeanServerConnection();  
    	ObjectName systemInfo = new ObjectName(SYSTEM_INFO);  
    	AttributeList attriList = connection.getAttributes(systemInfo, new String[]{"CommittedVirtualMemorySize","FreePhysicalMemorySize",
    			"FreeSwapSpaceSize","ProcessCpuTime","TotalPhysicalMemorySize","TotalSwapSpaceSize",
    			"Name","AvailableProcessors","Arch","SystemLoadAverage","Version"});
    	Iterator iter = attriList.iterator();
    	Map<String,Object> operationMap = new HashMap<String,Object>();
    	while(iter.hasNext()) {
    		Attribute attr = (Attribute)iter.next();
    		operationMap.put(attr.getName(), attr.getValue());
    	}
    	systemInformation.getSystemProperties().put("os.name", operationMap.get("Name"));
    	systemInformation.getSystemProperties().put("os.arch", operationMap.get("Arch"));
    	systemInformation.getSystemProperties().put("os.version", operationMap.get("Version"));
    	systemInformation.setCpuCount((Integer)operationMap.get("AvailableProcessors"));
    	
    	ObjectName memoryInfo = new ObjectName(MEMORY_INFO);  
    	CompositeDataSupport vmInfo = (CompositeDataSupport)connection.getAttribute(memoryInfo, "HeapMemoryUsage");
    	long commitedMemory = (Long)vmInfo.get("committed");
    	long freeMemory = commitedMemory - (Long)vmInfo.get("used");
    	systemInformation.setFreeMemory(freeMemory/1024/1024);
    	systemInformation.setTotalMemory(commitedMemory/1024/1024);
    	systemInformation.setMaxMemory((Long)vmInfo.get("max")/1024/1024);
    	
    	ObjectName runtimeInfo = new ObjectName(RUNTIME_INFO);
    	TabularDataSupport  dataSupport = (TabularDataSupport)connection.getAttribute(runtimeInfo, "SystemProperties");
    	for(Object su : dataSupport.entrySet()) {
    		CompositeDataSupport composite = ((CompositeDataSupport)((Entry)su).getValue());
    		systemInformation.getSystemProperties().put(composite.get("key").toString(),composite.get("value"));
    	}
    	System.out.println(systemInformation.getSystemProperties());
    	return systemInformation;
	}
	
	public List pullThreadListInfo(RemoteServerInfo remoteServerInfo) throws Exception {
		ObjectName threadOb = new ObjectName(THREAD_LIST_INFO);
		JMXServiceURL serviceURL = new JMXServiceURL(remoteServerInfo.getJmxUrl()); 
    	JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    	MBeanServerConnection connection = connector.getMBeanServerConnection();  
    	List threadList = new ArrayList();
    	long[] threadIds = (long[])connection.getAttribute(threadOb, "AllThreadIds");
    	long[] deadlockedIds = (long[]) connection.invoke(threadOb, "findMonitorDeadlockedThreads", null, null);
    	for(long threadId : threadIds) {
    		CompositeDataSupport result = (CompositeDataSupport)connection.invoke(threadOb, "getThreadInfo", new Object[]{threadId}, new String[]{"long"});
    		SunThread tm = new SunThread();
    		tm.setName(result.get("threadName").toString());
    		tm.setId(threadId);
    		tm.setState(result.get("threadState").toString());
    		tm.setSuspended((Boolean)result.get("suspended"));
    		tm.setInNative((Boolean)result.get("inNative"));
    		tm.setLockName(result.get("lockName")==null?"":result.get("lockName").toString());
    		tm.setLockOwnerName(result.get("lockOwnerName")==null?"":result.get("lockOwnerName").toString());
    		tm.setWaitedCount((Long)result.get("waitedCount"));
    		tm.setBlockedCount((Long)result.get("blockedCount"));
    		tm.setDeadlocked(contains(deadlockedIds, threadId));
    		
    		CompositeData[] stack = (CompositeData[]) result.get("stackTrace");
            if (stack.length > 0) {
                CompositeData cd2 = stack[0];
                ThreadStackElement tse = new ThreadStackElement();
                tse.setClassName(JmxTools.getStringAttr(cd2, "className"));
                tse.setFileName(JmxTools.getStringAttr(cd2, "fileName"));
                tse.setMethodName(JmxTools.getStringAttr(cd2, "methodName"));
                tse.setLineNumber(JmxTools.getIntAttr(cd2, "lineNumber", -1));
                tse.setNativeMethod(JmxTools.getBooleanAttr(cd2, "nativeMethod"));
                tm.setExecutionPoint(tse);
            }
            threadList.add(tm);
    	}
		return threadList;
	}
	
	private static boolean contains(long[] array, long e) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == e) {
                    return true;
                }
            }
        }
        return false;
    }
	
	@SuppressWarnings("rawtypes")
	public Map<String,Object> putAttributeToMap(AttributeList attriList) {
		Iterator iter = attriList.iterator();
		Map<String,Object> attMap = new HashMap<String,Object>();
    	while(iter.hasNext()) {
    		Attribute objs = (Attribute)iter.next();
    		attMap.put(objs.getName(), objs.getValue());
    	}
    	return attMap;
	}
	
	public void putBasicInfo(Application app,Map<String,Object> attMap) {
		if(attMap.size() > 0) {
			app.setName(attMap.get("name").toString());
	    	app.setDisplayName(null != attMap.get("displayName")?attMap.get("displayName").toString():"");
	    	app.setRequestCount((Integer)attMap.get("requestCount"));
	    	app.setSessionTimeout((Integer)attMap.get("sessionTimeout"));
	    	app.setDistributable((Boolean)attMap.get("distributable"));
		}
	}
	
	public void isServerAlive(Application app,RemoteServerInfo remoteServerInfo) {
		try {
			HttpGet httpgets = new HttpGet(remoteServerInfo.getHttpTestUrl());  
			long start = System.currentTimeMillis();
			HttpResponse response = client.execute(httpgets);
			long end = System.currentTimeMillis();
			app.setHttpCostTime(end-start);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if(null != entity) {
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,HTTP.UTF_8));      
				    String line = null;      
			        try {      
			            while ((line = reader.readLine()) != null) {  
			                if(line.indexOf(remoteServerInfo.getKeyWords()) != -1) {
			                	app.setAvailable(true);
			                }
			            }      
			        } catch (IOException e) {     
			        } finally {      
			            try {      
			            	inputStream.close();      
			            } catch (IOException e) {      
			            }      
			        }      
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		JmxServerInfoPuller jsi = new JmxServerInfoPuller();
		jsi.pullSystemInformation(null);
		//System.out.println((52154368-30092328)/1024/1024);
	}
	
}
