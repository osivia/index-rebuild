<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<c:set var="namespace"><portlet:namespace/></c:set>


<div class="saved-searches">
    <c:choose>
        <c:when test="${empty form.savedSearches}">
            <p class="text-secondary"><op:translate key="SAVED_SEARCHES_EMPTY"/></p>
        </c:when>

        <c:otherwise>
            <c:forEach var="savedSearch" items="${form.savedSearches}">
                <div class="position-relative mb-3">
                    <portlet:actionURL name="search" var="url">
                        <portlet:param name="id" value="${savedSearch.id}"/>
                    </portlet:actionURL>
                    <a href="${url}" title="${savedSearch.displayName}" class="btn btn-link btn-link-hover-primary-light btn-block text-left text-black bg-white">
                        <strong>${savedSearch.displayName}</strong>
                    </a>

                    <button type="button" class="btn btn-link position-absolute" data-toggle="modal" data-target="#${namespace}-delete-modal" data-id="${savedSearch.id}">
                        <i class="glyphicons glyphicons-basic-bin"></i>
                        <span class="sr-only"><op:translate key="DELETE"/></span>
                    </button>
                </div>
            </c:forEach>


            <div class="modal fade" id="${namespace}-delete-modal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="h5 modal-title"><op:translate key="SAVED_SEARCHES_DELETE_MODAL_TITLE"/></h3>

                            <button type="button" class="close" data-dismiss="modal">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>

                        <div class="modal-body">
                            <p><op:translate key="SAVED_SEARCHES_DELETE_MODAL_BODY"/></p>

                            <portlet:actionURL name="delete" var="url" />
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
