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
package com.googlecode.psiprobe;

import com.googlecode.psiprobe.model.FilterMapping;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.FilterMap;
import org.apache.commons.modeler.Registry;

/**
 * 
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class Tomcat60ContainerAdaptor extends AbstractTomcatContainer {

    private Host host;
    private ObjectName deployerOName;
    private MBeanServer mBeanServer;
    private Valve valve = new Tomcat60AgentValve();

    public void setWrapper(Wrapper wrapper) {
        if (wrapper != null) {
            host = (Host) wrapper.getParent().getParent();
            try {
                deployerOName = new ObjectName(host.getParent().getName() + ":type=Deployer,host=" + host.getName());
            } catch (MalformedObjectNameException e) {
                // do nothing here
            }
            host.getPipeline().addValve(valve);
            mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        } else if (host != null) {
            host.getPipeline().removeValve(valve);
        }
    }

    public boolean canBoundTo(String binding) {
        return binding != null && (binding.startsWith("Apache Tomcat/6.0")
                || binding.startsWith("JBossWeb/2.0")
                || binding.startsWith("JBoss Web/2.1")
                || (binding.startsWith("SpringSource tc") && binding.contains("/6.0"))
                || (binding.startsWith("VMware vFabric tc") && binding.contains("/6.0")));
    }

    protected Context findContextInternal(String name) {
        return (Context) host.findChild(name);
    }

    public List findContexts() {
        Container[] containers = host.findChildren();
        return Arrays.asList(containers);
    }

    public void stop(String name) throws Exception {
        Context ctx = findContext(name);
        if (ctx != null) {
            ((Lifecycle) ctx).stop();
        }
    }

    public void start(String name) throws Exception {
        Context ctx = findContext(name);
        if (ctx != null) {
            ((Lifecycle) ctx).start();
        }
    }

    private void checkChanges(String name) throws Exception {
        Boolean result = (Boolean) mBeanServer.invoke(deployerOName,
                        "isServiced", new String[]{name}, new String[]{"java.lang.String"});
        if (!result.booleanValue()) {
            mBeanServer.invoke(deployerOName, "addServiced",
                    new String[]{name}, new String[]{"java.lang.String"});
            try {
                mBeanServer.invoke(deployerOName, "check",
                        new String[]{name}, new String[]{"java.lang.String"});
            } finally {
                mBeanServer.invoke(deployerOName, "removeServiced",
                        new String[]{name}, new String[]{"java.lang.String"});
            }
        }
    }

    public void removeInternal(String name) throws Exception {
        checkChanges(name);
    }

    public void installWar(String name, URL url) throws Exception {
        checkChanges(name);
    }

    public void installContextInternal(String name, File config) throws Exception {
        checkChanges(name);
    }

    public File getAppBase() {
        File base = new File(host.getAppBase());
        if (! base.isAbsolute()) {
            base = new File(System.getProperty("catalina.base"), host.getAppBase());
        }
        return base;
    }

    public String getConfigBase() {
        return getConfigBase(host);
    }

    public Object getLogger(Context context) {
        return context.getLogger();
    }

    public String getHostName() {
        return host.getName();
    }

    public String getName() {
        return host.getParent().getName();
    }
    
//    protected List getFilterMappings(FilterMap fmap, String dm, String filterClass) {
//        String[] urls = fmap.getURLPatterns();
//        String[] servlets = fmap.getServletNames();
//        List filterMappings = new ArrayList(urls.length + servlets.length);
//        for (int i = 0; i < urls.length; i++) {
//            FilterMapping fm = new FilterMapping();
//            fm.setUrl(urls[i]);
//            fm.setFilterName(fmap.getFilterName());
//            fm.setDispatcherMap(dm);
//            fm.setFilterClass(filterClass);
//            filterMappings.add(fm);
//        }
//        for (int i = 0; i < servlets.length; i++) {
//            FilterMapping fm = new FilterMapping();
//            fm.setServletName(servlets[i]);
//            fm.setFilterName(fmap.getFilterName());
//            fm.setDispatcherMap(dm);
//            fm.setFilterClass(filterClass);
//            filterMappings.add(fm);
//        }
//        return filterMappings;
//    }
    
}
