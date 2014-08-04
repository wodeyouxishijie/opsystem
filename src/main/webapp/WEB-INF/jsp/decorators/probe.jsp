<%--
 * Licensed under the GPL License.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/tld/probe.tld" prefix="probe" %>


<%--
	Main site decorator. Face of the Probe.

	Author: Vlad Ilyushchenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="${lang}" xml:lang="${lang}">
	<head>
		<title>Probe - <decorator:title default="Tomcat management"/></title>
		<link type="image/gif" rel="shortcut icon" href="<c:url value='/css/favicon.gif'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='tables.css'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='main.css'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='mainnav.css'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='messages.css'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='tooltip.css'/>"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='jquery.treeview.css'/>"/>
		<script type="text/javascript" language="javascript" src="<c:url value='/js/jquery.js'/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value='/js/jquery.cookie.js'/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value='/js/jquery.treeview.js'/>"></script>
		<decorator:head/>
	</head>
	
	<script>
		jQuery(document).ready(function(){
			jQuery("#browser").treeview();
		});
	</script>
	<body>

		<div id="caption">
			<ul id="top">
				<li id="logo"><a href="<c:url value='/index.htm'/>"><img src="<c:url value='/css/the-probe-logo.gif'/>"
																		alt="PSI Probe Logo"/></a></li>
				<li id="runtime">
					<spring:message code="probe.jsp.version" arguments="${version},<b>${hostname}</b>"/>,
					<span class="uptime"><spring:message code="probe.jsp.uptime"
														arguments="${uptime_days},${uptime_hours},${uptime_mins}"/></span></li>
				<li id="title"><decorator:title default="Probe"/></li>
			</ul>
		</div>
		<div id="menutree" style="width:15%;float:left;margin-left:10px;margin-top:10px;border:1px solid #C1DAD7;padding-top:10px;padding-left:10px;">
			Menu
			<ul id="browser" class="filetree">
				<li ><span class="folder">&nbsp;WeChat</span>
					<ul>
						<li><span class="folder">&nbsp;Tomcat</span>
							<ul id="folder21">
								<li><span class="file">&nbsp;<a href="index.htm?serverId=1">Tomcat Server 1</a></span></li>
								<li><span class="file">&nbsp;<a href="index.htm?serverId=2">Tomcat Server 2</a></span></li>
							</ul>
						</li>
						<li><span class="folder">&nbsp;Node</span>
							<ul id="folder21">
								<li><span class="file">Node Server 1</span></li>
								<li><span class="file">Node Server 2</span></li>
							</ul>
						</li>
					</ul>
				</li>
				<!--
				<li class="closed">
					<span class="folder">&nbsp;Folder 3 (closed at start)</span>
					<ul>
						<li><span class="file">Project3</span></li>
					</ul>
				</li>
				<li><span class="file">File 4</span></li> -->
			</ul>
		</div>
		<div style="width:80%;float:left;margin-top:10px;">
			<div id="navcontainer" >
				<ul id="tabnav">
					<li>
						<a class="${navTabApps}" href="<c:url value='/index.htm?size=${param.size}'/>">
							<spring:message code="probe.jsp.menu.applications"/>
						</a>
					</li>
					<li>
						<a class="${navTabDatasources}" href="<c:url value='/datasources.htm'/>">
							<spring:message code="probe.jsp.menu.datasources"/>
						</a>
					</li>
					<li>
						<a class="${navTabDeploy}" href="<c:url value='/adm/deploy.htm'/>">
							<spring:message code="probe.jsp.menu.deployment"/>
						</a>
					</li>
					<li>
						<a class="${navTabLogs}" href="<c:url value='/logs/index.htm'/>">
							<spring:message code="probe.jsp.menu.logs"/>
						</a>
					</li>
					<li>
						<a class="${navTabThreads}" href="<c:url value='/threads.htm'/>">
							<spring:message code="probe.jsp.menu.threads"/>
						</a>
					</li>
					<li>
						<a class="${navTabCluster}" href="<c:url value='/cluster.htm'/>">
							<spring:message code="probe.jsp.menu.cluster"/>
						</a>
					</li>
					<li>
						<a class="${navTabSystem}" href="<c:url value='/sysinfo.htm'/>">
							<spring:message code="probe.jsp.menu.sysinfo"/>
						</a>
					</li>
					<li>
						<a class="${navTabConnectors}" href="<c:url value='/connectors.htm'/>">
							<spring:message code="probe.jsp.menu.connectors"/>
						</a>
					</li>
					<li>
						<a class="${navTabQuickCheck}" href="<c:url value='/adm/quickcheck.htm'/>">
							<spring:message code="probe.jsp.menu.quickcheck"/>
						</a>
					</li>
				</ul>
			</div>

			<c:choose>
				<c:when test="${! empty use_decorator}">
					<page:applyDecorator name="${use_decorator}">
						<decorator:body/>
					</page:applyDecorator>
				</c:when>
				<c:otherwise>
					<div id="mainBody">
						<decorator:body/>
					</div>
				</c:otherwise>
			</c:choose>

			<div id="footer">
				<ul>
					<li>
						<a href="<c:url value='/index.htm'/>">
							<spring:message code="probe.jsp.menu.applications"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/datasources.htm'/>">
							<spring:message code="probe.jsp.menu.datasources"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/adm/deploy.htm'/>">
							<spring:message code="probe.jsp.menu.deployment"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/logs/index.htm'/>">
							<spring:message code="probe.jsp.menu.logs"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/threads.htm'/>">
							<spring:message code="probe.jsp.menu.threads"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/cluster.htm'/>">
							<spring:message code="probe.jsp.menu.cluster"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/sysinfo.htm'/>">
							<spring:message code="probe.jsp.menu.sysinfo"/>
						</a>
					</li>
					<li>
						<a href="<c:url value='/connectors.htm'/>">
							<spring:message code="probe.jsp.menu.connectors"/>
						</a>
					</li>
					<li class="last">
						<a href="<c:url value='/adm/quickcheck.htm'/>">
							<spring:message code="probe.jsp.menu.quickcheck"/>
						</a>
					</li>
				</ul>
				<p>
					<spring:message code="probe.jsp.copyright"/>
					<br/>
					<spring:message code="probe.jsp.icons.credit"/>
				</p>
				<p>
					<spring:message code="probe.jsp.i18n.credit"/>
				</p>
			</div>
		</div>
	</body>
</html>