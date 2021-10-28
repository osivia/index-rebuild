<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page contentType="text/html;charset=UTF-8" %>


<portlet:actionURL name="drop" var="dropUrl"/>
<portlet:actionURL name="search" var="searchUrl"/>
<portlet:actionURL name="save-collapse-state" var="saveCollapseStateUrl"/>

<portlet:resourceURL id="lazy-loading" var="lazyLoadingUrl"/>


<c:set var="namespace"><portlet:namespace/></c:set>

<c:set var="select2Searching"><op:translate key="SELECT2_SEARCHING"/></c:set>
<c:set var="select2NoResults"><op:translate key="SELECT2_NO_RESULTS"/></c:set>


<div class="taskbar" data-drop-url="${dropUrl}" data-lazy-loading-url="${lazyLoadingUrl}" data-state-method="taskbarState">
    <ul class="list-unstyled">
        <%--@elvariable id="taskbar" type="fr.index.cloud.ens.taskbar.portlet.model.Taskbar"--%>
        <c:forEach var="task" items="${taskbar.tasks}" varStatus="status">
            <li>
                <c:choose>
                    <%--Add--%>
                    <c:when test="${task.add}">
                        <c:if test="${not empty task.dropdownItems}">
                            <div class="dropdown mb-4">
                                <button class="dropdown-toggle btn btn-primary btn-block rounded-pill text-white text-truncate"
                                        data-toggle="dropdown"
                                        data-boundary="window">
                                    <i class="glyphicons glyphicons-basic-plus"></i>
                                    <strong><op:translate key="TASKBAR_ADD"/></strong>
                                </button>

                                <div class="dropdown-menu">
                                    <c:forEach var="dropdownItem" items="${task.dropdownItems}">
                                        <a href="javascript:" class="dropdown-item" data-target="#osivia-modal"
                                           data-load-url="${dropdownItem.url}" data-title="${dropdownItem.modalTitle}">
                                            <c:choose>
                                                <c:when test="${empty dropdownItem.customizedIcon}"><i
                                                        class="${dropdownItem.icon}"></i></c:when>
                                                <c:otherwise>${dropdownItem.customizedIcon}</c:otherwise>
                                            </c:choose>
                                            <span>${dropdownItem.displayName}</span>
                                        </a>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </c:when>

                    <%--Navigation tree--%>
                    <c:when test="${task.fancytree}">
                        <div class="fancytree overflow-hidden">
                            <ul class="ml-4">
                                <li class="${task.active ? 'current' : ''}"
                                    data-retain="${task.selected}"
                                    data-acceptedtypes="${fn:join(task.acceptedTypes, ',')}"
                                    data-expanded="${task.selected}" data-folder="${task.folder}"
                                    data-lazy="${task.lazy}" data-current="${task.active}" data-id="${task.id}"
                                    data-path="${task.path}">
                                    <a href="${task.url}" class="pl-0">
                                        <span>${task.displayName}</span>
                                    </a>

                                    <!-- Children -->
                                    <c:set var="parent" value="${task}" scope="request"/>
                                    <ttc:include page="folder-children.jsp"/>
                                </li>
                            </ul>
                        </div>
                    </c:when>

                    <%--Filters--%>
                    <c:when test="${task.filters}">
                        <hr class="mt-5">

                        <div class="mb-3">
                            <a href="#${namespace}-filters"
                               class="d-flex align-items-center text-secondary text-decoration-none no-ajax-link"
                               data-toggle="collapse">
                                <small class="flex-shrink-0 mr-1">
                                    <i class="glyphicons glyphicons-basic-set-down"></i>
                                </small>

                                <strong class="flex-grow-1 text-black text-truncate"><op:translate
                                        key="TASKBAR_FILTERS"/></strong>
                            </a>

                            <div id="${namespace}-filters" class="collapse ${taskbar.showFilters ? 'show' : ''} ml-4" data-url="${saveCollapseStateUrl}" data-id="filters">
                                <%--Save current search--%>
                                <div class="d-flex justify-content-end my-3 ml-auto">
                                    <portlet:actionURL name="advanced-search-new-filter" var="url">
                                        <portlet:param name="titleKey" value="TASKBAR_SAVE_CURRENT_SEARCH"/>
                                    </portlet:actionURL>
                                    <a href="${url}" class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate no-ajax-link">
                                        <strong><op:translate key="TASKBAR_SAVE_CURRENT_SEARCH"/></strong>
                                    </a>
                                </div>

                                <c:choose>
                                    <c:when test="${empty task.savedSearches}">
                                        <p class="my-3 text-secondary"><op:translate
                                                key="TASKBAR_SAVED_SEARCHES_EMPTY"/></p>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="savedSearch" items="${task.savedSearches}">
                                            <div class="position-relative mb-3">
                                                <portlet:actionURL name="saved-search" var="url">
                                                    <portlet:param name="id" value="${savedSearch.url}"/>
                                                </portlet:actionURL>
                                                <a href="${url}" title="${savedSearch.displayName}" class="btn btn-link btn-link-hover-primary-light btn-block text-left text-black bg-white">
                                                    <strong>${savedSearch.displayName}</strong>
                                                </a>

                                                <button type="button" class="btn btn-link position-absolute" data-toggle="modal" data-target="#${namespace}-delete-filter-modal" data-id="${savedSearch.url}">
                                                    <i class="glyphicons glyphicons-basic-bin"></i>
                                                    <span class="sr-only"><op:translate key="DELETE"/></span>
                                                </button>
                                            </div>
                                        </c:forEach>


                                        <div class="modal fade" id="${namespace}-delete-filter-modal">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h3 class="h5 modal-title"><op:translate key="TASKBAR_DELETE_FILTER_MODAL_TITLE"/></h3>

                                                        <button type="button" class="close" data-dismiss="modal">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>

                                                    <div class="modal-body">
                                                        <p><op:translate key="TASKBAR_DELETE_FILTER_MODAL_BODY"/></p>

                                                        <portlet:actionURL name="delete-saved-search" var="url" />
                                                        <form action="${url}" method="post">
                                                            <input type="hidden" name="id" value="">
                                                            <input type="submit" class="d-none">

                                                            <div class="text-right">
                                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                                                                    <span><op:translate key="CANCEL"/></span>
                                                                </button>

                                                                <button type="button" class="btn btn-primary" data-dismiss="modal" data-submit>
                                                                    <span><op:translate key="DELETE"/></span>
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:when>

                    <%--Search--%>
                    <c:when test="${task.search}">
                        <hr class="mt-5">

                        <div class="mb-3">
                            <a href="#${namespace}-search"
                               class="d-flex align-items-center text-secondary text-decoration-none no-ajax-link"
                               data-toggle="collapse">
                                <small class="flex-shrink-0 mr-1">
                                    <i class="glyphicons glyphicons-basic-set-down"></i>
                                </small>

                                <strong class="flex-grow-1 text-black text-truncate">
                                    <span><op:translate key="TASKBAR_SEARCH"/></span>
                                    <c:if test="${task.counter gt 0}">
                                        <span>(${task.counter})</span>
                                    </c:if>
                                </strong>
                            </a>

                            <div id="${namespace}-search" class="collapse ${taskbar.showSearch ? 'show' : ''}" data-url="${saveCollapseStateUrl}" data-id="search">
                                <div class="d-flex  overflow-hidden text-nowrap mt-3 mb-2">
                                    <%--Advanced search--%>
                                    <div class="d-flex justify-content-end mb-2">
                                        <portlet:actionURL name="advanced-search" var="url"/>
                                        <a href="${url}" class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate no-ajax-link">
                                            <i class="glyphicons glyphicons-basic-plus"></i>
                                            <strong><op:translate key="TASKBAR_ADVANCED_SEARCH"/></strong>
                                        </a>
                                    </div>

                                    <%--Reset--%>
                                    <portlet:actionURL var="resetUrl" name="reset"/>
                                    <div class="d-flex justify-content-end mb-2 ml-auto">
                                        <a href="${resetUrl}"
                                           class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate">
                                            <i class="glyphicons glyphicons-basic-reload"></i>
                                            <strong><op:translate key="TASKBAR_RESET_SEARCH"/></strong>
                                        </a>
                                    </div>
                                </div>

                                <%--@elvariable id="searchForm" type="fr.index.cloud.ens.taskbar.portlet.model.TaskbarSearchForm"--%>
                                <form:form action="${searchUrl}" method="post" modelAttribute="searchForm">
                                    <%--Keywords--%>
                                    <c:set var="placeholder"><op:translate key="TASKBAR_SEARCH_KEYWORDS"/></c:set>
                                    <div class="form-group">
                                        <form:label path="keywords" cssClass="sr-only">${placeholder}</form:label>
                                        <div class="input-group">
                                            <form:input path="keywords" type="search" cssClass="form-control"
                                                        placeholder="${placeholder}" data-restore-normal-view-on-focus="true" />
                                            <div class="input-group-append">
                                                <span class="input-group-text">
                                                    <i class="glyphicons glyphicons-basic-search"></i>
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    <%--Document type--%>
                                    <portlet:resourceURL id="load-vocabulary" var="loadUrl">
                                        <portlet:param name="vocabulary" value="idx_document_type"/>
                                    </portlet:resourceURL>
                                    <c:set var="placeholder"><op:translate
                                            key="TASKBAR_SEARCH_DOCUMENT_TYPE"/></c:set>
                                    <div class="form-group">
                                        <form:label path="documentTypes"
                                                    cssClass="sr-only">${placeholder}</form:label>
                                        <form:select id="${namespace}-documentTypes" path="documentTypes"
                                                     cssClass="form-control select2 select2-default"
                                                     data-placeholder="${placeholder}" data-url="${loadUrl}"
                                                     data-searching="${select2Searching}"
                                                     data-no-results="${select2NoResults}">
                                            <c:forEach var="item" items="${searchForm.documentTypes}">
                                                <form:option value="${item}"><ttc:vocabularyLabel
                                                        name="idx_document_type" key="${item}"/></form:option>
                                            </c:forEach>
                                        </form:select>
                                    </div>

                                    <%--Level--%>
                                    <portlet:resourceURL id="load-vocabulary" var="loadUrl">
                                        <portlet:param name="vocabulary" value="idx_level"/>
                                    </portlet:resourceURL>
                                    <c:set var="placeholder"><op:translate key="TASKBAR_SEARCH_LEVEL"/></c:set>
                                    <div class="form-group">
                                        <form:label path="levels" cssClass="sr-only">${placeholder}</form:label>
                                        <form:select id="${namespace}-levels" path="levels" cssClass="form-control select2 select2-default"
                                                     data-placeholder="${placeholder}" data-url="${loadUrl}"
                                                     data-searching="${select2Searching}"
                                                     data-no-results="${select2NoResults}">
                                            <c:forEach var="item" items="${searchForm.levels}">
                                                <form:option value="${item}"><ttc:vocabularyLabel name="idx_level"
                                                                                                  key="${item}"/></form:option>
                                            </c:forEach>
                                        </form:select>
                                    </div>

                                    <%--Subject--%>
                                    <portlet:resourceURL id="load-vocabulary" var="loadUrl">
                                        <portlet:param name="vocabulary" value="idx_subject"/>
                                    </portlet:resourceURL>
                                    <c:set var="placeholder"><op:translate key="TASKBAR_SEARCH_SUBJECT"/></c:set>
                                    <div class="form-group">
                                        <form:label path="subjects" cssClass="sr-only">${placeholder}</form:label>
                                        <form:select id="${namespace}-subjects" path="subjects" cssClass="form-control select2 select2-default"
                                                     data-placeholder="${placeholder}" data-url="${loadUrl}"
                                                     data-searching="${select2Searching}"
                                                     data-no-results="${select2NoResults}">
                                            <c:forEach var="item" items="${searchForm.subjects}">
                                                <form:option value="${item}"><ttc:vocabularyLabel name="idx_subject"
                                                                                                  key="${item}"/></form:option>
                                            </c:forEach>
                                        </form:select>
                                    </div>

                                    <input type="submit" class="d-none">
                                </form:form>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <a href="${task.url}"
                           class="d-flex align-items-center mb-3 ml-4 ${task.active ? 'text-primary-light' : 'text-secondary'} text-decoration-none text-truncate">
                            <i class="${task.icon}"></i>
                            <strong class="ml-2 text-black">${task.displayName}</strong>
                        </a>
                    </c:otherwise>
                </c:choose>
            </li>
        </c:forEach>
    </ul>
</div>
