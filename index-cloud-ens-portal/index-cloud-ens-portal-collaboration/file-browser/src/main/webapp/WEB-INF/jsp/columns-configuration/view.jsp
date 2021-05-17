<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="save" var="url"/>

<c:set var="namespace"><portlet:namespace/></c:set>


<%--@elvariable id="form" type="fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationForm"--%>
<div class="file-browser-columns-configuration">
    <form:form action="${url}" method="post" modelAttribute="form">
        <div class="form-group">
            <ol class="file-browser-columns-sortable">
                <c:forEach var="item" items="${form.items}" varStatus="status">
                    <li class="mb-3">
                        <div class="card">
                            <div class="card-body p-2">
                                <div class="d-flex align-items-center">
                                    <div class="mr-2">
                                        <a href="javascript:" class="p-3 text-secondary file-browser-columns-sortable-handle">
                                            <i class="glyphicons glyphicons-basic-sort"></i>
                                            <span class="sr-only"><op:translate key="MOVE"/></span>
                                        </a>
                                    </div>

                                    <div>
                                        <h3 class="h5 card-title mb-2">${item.title}</h3>
                                        <div class="form-check">
                                            <form:checkbox id="${namespace}-visibility-${status.index}" path="items[${status.index}].visible" cssClass="form-check-input"/>
                                            <form:label for="${namespace}-visibility-${status.index}" path="items[${status.index}].visible" cssClass="form-check-label">
                                                <span><op:translate key="FILE_BROWSER_COLUMNS_CONFIGURATION_VISIBILITY_CHECKBOX_LABEL"/></span>
                                                <c:if test="${item.listMode}">
                                                    <small class="text-secondary"><op:translate key="FILE_BROWSER_COLUMNS_CONFIGURATION_VISIBILITY_LIST_MODE_HELP"/></small>
                                                </c:if>
                                            </form:label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <form:hidden path="items[${status.index}].order"/>
                    </li>
                </c:forEach>
            </ol>
            <p class="form-text">
                <small class="text-muted"><op:translate key="FILE_BROWSER_COLUMNS_CONFIGURATION_ITEMS_HELP"/></small>
            </p>
        </div>

        <%--Buttons--%>
        <div class="text-right">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">
                <span><op:translate key="CANCEL"/></span>
            </button>

            <button type="submit" class="btn btn-primary">
                <span><op:translate key="SAVE"/></span>
            </button>
        </div>
    </form:form>
</div>
