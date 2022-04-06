<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="enable" var="enableUrl"/>
<portlet:actionURL name="disable" var="disableUrl"/>

<portlet:resourceURL id="load-vocabulary" var="loadDocumentTypesUrl">
    <portlet:param name="vocabulary" value="idx_document_type"/>
</portlet:resourceURL>
<portlet:resourceURL id="load-vocabulary" var="loadLevelsUrl">
    <portlet:param name="vocabulary" value="idx_level"/>
</portlet:resourceURL>
<portlet:resourceURL id="load-vocabulary" var="loadSubjectsUrl">
    <portlet:param name="vocabulary" value="idx_subject"/>
</portlet:resourceURL>
<portlet:resourceURL id="load-vocabulary" var="loadLicencesUrl">
    <portlet:param name="vocabulary" value="idx_licence"/>
</portlet:resourceURL>



<c:set var="select2Searching"><op:translate key="SELECT2_SEARCHING"/></c:set>
<c:set var="select2NoResults"><op:translate key="SELECT2_NO_RESULTS"/></c:set>


<form:form action="${enableUrl}" method="post" modelAttribute="form">
    <%--Title--%>
    <div class="form-group required">
        <form:label path="title"><op:translate key="MUTUALIZATION_TITLE"/></form:label>
        <form:input path="title" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
        <form:errors path="title" cssClass="invalid-feedback" />
    </div>

    <%--Keywords--%>
    <div class="form-group required">
        <form:label path="keywords"><op:translate key="MUTUALIZATION_KEYWORDS"/></form:label>
        <small class="form-text text-muted pb-1"><op:translate key="MUTUALIZATION_KEYWORDS_SEPARATE"/></small> 
        <form:select path="keywords" cssClass="form-control select2 select2-default" cssErrorClass="form-control is-invalid select2 select2-default" data-tags="true" data-no-results="${select2NoResults}">
            <form:options items="${form.suggestedKeywords}" />
        </form:select>
        <form:errors path="keywords" cssClass="invalid-feedback" />
    </div>

    <%--Document types--%>
    <div class="form-group required">
        <form:label path="documentTypes"><op:translate key="MUTUALIZATION_DOCUMENT_TYPES"/></form:label>
        <form:select path="documentTypes" cssClass="form-control select2 select2-default" cssErrorClass="form-control is-invalid select2 select2-default" data-url="${loadDocumentTypesUrl}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:forEach var="documentType" items="${form.documentTypes}">
                <form:option value="${documentType}"><ttc:vocabularyLabel name="idx_document_type" key="${documentType}"/></form:option>
            </c:forEach>
        </form:select>
        <form:errors path="documentTypes" cssClass="invalid-feedback" />
    </div>

    <%--Levels--%>
    <div class="form-group required">
        <form:label path="levels"><op:translate key="MUTUALIZATION_LEVELS"/></form:label>
        <form:select path="levels" cssClass="form-control select2 select2-default" cssErrorClass="form-control is-invalid select2 select2-default" data-url="${loadLevelsUrl}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:forEach var="level" items="${form.levels}">
                <form:option value="${level}"><ttc:vocabularyLabel name="idx_level" key="${level}"/></form:option>
            </c:forEach>
        </form:select>
        <form:errors path="levels" cssClass="invalid-feedback" />
    </div>

    <%--Subjects--%>
    <div class="form-group required">
        <form:label path="subjects"><op:translate key="MUTUALIZATION_SUBJECTS"/></form:label>
        <form:select path="subjects" cssClass="form-control select2 select2-default" cssErrorClass="form-control is-invalid select2 select2-default" data-url="${loadSubjectsUrl}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:forEach var="subject" items="${form.subjects}">
                <form:option value="${subject}"><ttc:vocabularyLabel name="idx_subject" key="${subject}"/></form:option>
            </c:forEach>
        </form:select>
        <form:errors path="subjects" cssClass="invalid-feedback" />
    </div>

    <%--Licence --%>
    <div class="form-group required">
    	<div class="d-flex">
    		<div class="flex-grow-1">
		        <form:label path="licence"><op:translate key="MUTUALIZATION_LICENCE"/></form:label>
        	</div>
        	<div>
        	   <c:set var="popupTitle"><op:translate key="LICENCE_CC_TITLE"/></c:set>
      	         <a href="javascript:;" class="no-ajax-link html-popover border-0 p-0 m-0 text-secondary "  data-popover-style="popover-sized-image" data-popover-content="<img src='/index-cloud-ens-portal-mutualization/img/licences.png' >" data-popover-title="${popupTitle}">
                          <i class="glyphicons glyphicons-basic-circle-question"></i>
                          <span class="d-md-none"><op:translate key="HELP"/></span>
       			</a>
        	</div>
        </div>        	
        <form:select path="licence" cssClass="form-control select2 select2-default" cssErrorClass="form-control is-invalid select2 select2-default" data-url="${loadLicencesUrl}" data-searching="${select2Searching}" data-no-results="${select2NoResults}" data-sort="id">
			<c:if test='${not empty "${form.licence}"}'>
                <form:option value="${form.licence}"><ttc:vocabularyLabel name="idx_licence" key="${form.licence}"/></form:option>
            </c:if>
        </form:select>
        <form:errors path="licence" cssClass="invalid-feedback" />
    </div>  
    
     <%--Comment --%>
    <div class="form-group">
        <form:label path="comment"><op:translate key="MUTUALIZATION_COMMENT"/></form:label>
        <form:textarea path="comment" cssClass="form-control" rows="3" cssErrorClass="form-control is-invalid"/>
        <form:errors path="comment" cssClass="invalid-feedback" />
    </div>

    <%--Buttons--%>
    <div class="text-right">
        <%--Cancel--%>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

        <%--Disable--%>
        <c:if test="${form.enable}">
            <a href="${disableUrl}" class="btn btn-orange-dark ml-2">
                <span><op:translate key="MUTUALIZATION_DISABLE"/></span>
            </a>
        </c:if>
        
 
        <%--Enable--%>
        <button type="submit" class="btn btn-orange-dark ml-2">
            <span>
                <c:choose>
                    <c:when test="${form.enable}"><op:translate key="MUTUALIZATION_UPDATE"/></c:when>
                    <c:otherwise><op:translate key="MUTUALIZATION_ENABLE"/></c:otherwise>
                </c:choose>
            </span>
        </button>
    </div>

</form:form>
