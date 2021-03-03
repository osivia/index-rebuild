<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <%--Drawer toggle button--%>
    <ul class="navbar-nav d-md-none mr-3 drawer-toggle-button">
        <li class="nav-item">
            <a href="javascript:toggleDrawer()" class="nav-link" data-toggle="drawer">
                <i class="glyphicons glyphicons-basic-menu"></i>
                <span class="sr-only"><op:translate key="TOOLBAR_OPEN_NAVIGATION_MENU"/></span>
            </a>
        </li>
    </ul>


    <%--Brand--%>
    <a href="${requestScope['osivia.home.url']}" class="navbar-brand py-0">
        <img alt="${requestScope['osivia.header.application.name']}"
             src="${contextPath}/img/logo-cloud-pronote-toolbar.png" height="45">
    </a>


    <%--Navbar toggler--%>
    <div class="navbar-nav d-md-none">
        <a href="javascript:" class="navbar-link text-light text-decoration-none" data-toggle="collapse" data-target="#toolbar-navbar-collapse">
            <span><op:translate key="NAVBAR_TOGGLER"/></span>
            <i class="glyphicons glyphicons-basic-set-down"></i>
        </a>
    </div>


    <%--Navbar collapse--%>
    <div id="toolbar-navbar-collapse" class="collapse navbar-collapse mt-3 mt-md-0">
        <c:choose>
            <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                <%--Login--%>
                <div class="navbar-nav ml-md-auto">
                    <a href="${requestScope['osivia.toolbar.loginURL']}" class="nav-link text-light">
                        <i class="customized-icon customized-icon-user h4 mb-0 align-middle"></i>
                        <span><op:translate key="TOOLBAR_LOGIN"/></span>
                    </a>
                </div>
            </c:when>

            <c:otherwise>
                <%--Administration--%>
                <ul class="navbar-nav mr-md-3">
                    <li class="nav-item">
                        <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false"/>
                    </li>
                </ul>

                <%--Navigation--%>
                <ul class="index-tabs navbar-nav align-items-md-center flex-grow-1 mr-md-3">
                    <li class="nav-item mb-1 mb-md-0 flex-shrink-0">
                        <a href="${requestScope['osivia.default.memberPageUrl']}" class="index-tab index-tab-home text-light ${requestScope['osivia.default.memberPage'] ? 'active' : ''}">
                            <i class="glyphicons glyphicons-basic-home"></i>
                            <span class="d-md-none"><op:translate key="HOME"/></span>
                        </a>
                    </li>

                    <c:forEach var="navItem" items="${requestScope['osivia.nav.items']}" varStatus="status">
                        <li class="nav-item mb-1 mb-md-0 ml-md-4">
                            <a href="${navItem.url}" class="index-tab text-${navItem.color} ${empty navItem.url ? 'disabled' : ''} ${navItem.active ? 'active' : ''}">
                                <i class="${navItem.icon}"></i>
                                <span><op:translate key="${navItem.key}"/></span>
                            </a>
                        </li>
                    </c:forEach>
                </ul>

                <ul class="navbar-nav align-items-md-center flex-shrink-0">
                    <%--Tasks--%>
                    <li class="nav-item mr-md-2">
                        <%@ include file="toolbar-tasks.jspf" %>
                    </li>

                    <%--User--%>
                    <li class="nav-item mr-md-2">
                        <a href="${requestScope['osivia.my-account.url']}" class="nav-link d-flex align-items-center py-0 text-light">
                            <c:choose>
                                <c:when test="${empty requestScope['osivia.toolbar.person']}">
                                    <i class="glyphicons glyphicons-basic-user h4 mb-0 mr-2 align-middle"></i>
                                    <span class="text-user">${requestScope['osivia.toolbar.principal']}</span>
                                </c:when>

                                <c:otherwise>
                                    <img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}"
                                         alt="">
                                    <span class="text-user">${requestScope['osivia.toolbar.person'].cn}</span>
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </li>

                    <%--Logout--%>
                    <li class="nav-item">
                        <a href="javascript:" onclick="logout()" class="nav-link text-red">
                            <i class="glyphicons glyphicons-basic-power"></i>
                            <span class="d-md-none"><op:translate key="TOOLBAR_LOGOUT"/></span>
                        </a>
                    </li>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</nav>


<%--Disconnection modal--%>
<div id="disconnection" class="modal fade" data-apps="${op:join(requestScope['osivia.sso.applications'], '|')}"
     data-redirection="${requestScope['osivia.toolbar.signOutURL']}">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <i class="glyphicons glyphicons-basic-door"></i>
                <span><op:translate key="TOOLBAR_LOGOUT_MESSAGE"/></span>
            </div>
        </div>
    </div>

    <div class="apps-container d-none"></div>
</div>
