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
package com.googlecode.psiprobe.beans;

import org.apache.commons.dbcp.BasicDataSource;

import com.googlecode.psiprobe.model.DataSourceInfo;

public class TomcatDbcp2DatasourceAccessor implements DatasourceAccessor {

    public DataSourceInfo getInfo(Object resource) throws Exception {
        DataSourceInfo dataSourceInfo = null;
        if (canMap(resource)) {
            BasicDataSource source = (BasicDataSource) resource;
            dataSourceInfo = new DataSourceInfo();
            dataSourceInfo.setBusyConnections(source.getNumActive());
            dataSourceInfo.setEstablishedConnections(source.getNumIdle() + source.getNumActive());
            dataSourceInfo.setMaxConnections(source.getMaxActive()+source.getMaxIdle());
            dataSourceInfo.setJdbcURL(source.getUrl());
            dataSourceInfo.setUsername(source.getUsername());
            dataSourceInfo.setResettable(false);
        }
        return dataSourceInfo;
    }

    public boolean reset(Object resource) throws Exception {
        return false;
    }

    public boolean canMap(Object resource) {
        return resource.getClass().getName().equals("org.apache.tomcat.dbcp.dbcp2.BasicDataSource") && resource instanceof BasicDataSource;
    }

}
