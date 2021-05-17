<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<div class="file-browser">
    <%@ include file="toolbar.jspf" %>

    <%--Table--%>
    <div class="portal-table  overflow-x-hidden ">
        <%--Header--%>
        <%@ include file="table-header.jspf" %>

        <div class="portlet-filler overflow-y-auto overflow-x-hidden">
            <%--Body--%>
            <%@ include file="table-body.jspf" %>
        </div>
    </div>
</div>
