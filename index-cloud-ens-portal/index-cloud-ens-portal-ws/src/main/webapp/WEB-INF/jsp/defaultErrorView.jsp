<%@page import="fr.index.cloud.oauth.error.OAuthErrorMgr"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%
ApplicationContext context =  WebApplicationContextUtils.getWebApplicationContext(getServletContext());
OAuthErrorMgr errorMgr = (OAuthErrorMgr) context.getBean("OAuthErrorMgr");
errorMgr.handleError( request, response, (Exception) request.getAttribute ("exception"));
%>