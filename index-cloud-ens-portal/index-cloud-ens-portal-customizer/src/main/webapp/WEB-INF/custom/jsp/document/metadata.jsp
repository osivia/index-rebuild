<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="cancel-inline-edition" var="cancelUrl"/>

<c:set var="namespace"><portlet:namespace/></c:set>

<c:set var="searching"><op:translate key="SELECT2_SEARCHING"/></c:set>
<c:set var="noResults"><op:translate key="SELECT2_NO_RESULTS"/></c:set>

<div class="metadata">
    <div class="mb-3">
        <%--Mutualized title--%>
        <c:if test="${readOnly}">
            <h3 class="h4 mb-3">${document.properties['mtz:title']}</h3>
        </c:if>

        <%--Keywords--%>
        <c:set var="keywords" value="${document.properties['idxcl:keywords']}"/>
  
        <c:choose>
	        <c:when test="${readOnly}">
	            <p class="mb-2">
	                <strong><op:translate key="DOCUMENT_METADATA_KEYWORDS_LABEL"/></strong>
	            </p>
	            <ul class="list-inline">
	                <c:forEach var="keyword" items="${document.properties['idxcl:keywords']}">
	                    <li class="list-inline-item">
	                        <span class="badge badge-pill badge-orange-dark">${keyword}</span>
	                    </li>
	                </c:forEach>
	            </ul>
           </c:when>
            

            <c:otherwise>
                <c:set var="placeholder"><op:translate key="DOCUMENT_METADATA_KEYWORDS_PLACEHOLDER"/></c:set>
                <portlet:actionURL name="inline-edition" var="submitUrl">
                    <portlet:param name="property" value="idxcl:keywords"/>
                    <portlet:param name="cancel-url" value="${cancelUrl}"/>
                </portlet:actionURL>

                <form action="${submitUrl}" method="post">
                    <div class="form-group inline-edition">
                        <label for="${namespace}-keywords">
                            <strong><op:translate key="DOCUMENT_METADATA_KEYWORDS_LABEL"/></strong>
                        </label>
                        <select id="${namespace}-keywords" name="inline-values" multiple="multiple"
                                class="form-control select2 select2-inline-edition" data-tags="true"  data-no-results="${noResults}" data-searching="${searching}"
                                data-placeholder="${placeholder}">
                            <c:forEach var="keyword" items="${keywords}">
                                <option value="${keyword}" selected="selected">${keyword}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="submit" class="d-none">
                </form>
            </c:otherwise>           
        </c:choose>        

        <%--Levels--%>
        <c:set var="levels" value="${document.properties['idxcl:levels']}"/>
        <c:choose>
            <c:when test="${readOnly}">
                <p class="mb-2">
                    <strong><op:translate key="DOCUMENT_METADATA_LEVEL_LABEL"/></strong>
                </p>
                <ul class="list-inline">
                    <c:forEach var="level" items="${levels}">
                        <li class="list-inline-item">
                        <span class="badge badge-pill badge-orange-dark"><ttc:vocabularyLabel name="idx_level"
                                                                                              key="${level}"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>

            <c:otherwise>
                <c:set var="placeholder"><op:translate key="DOCUMENT_METADATA_LEVEL_PLACEHOLDER"/></c:set>
                <portlet:actionURL name="inline-edition" var="submitUrl">
                    <portlet:param name="property" value="idxcl:levels"/>
                    <portlet:param name="cancel-url" value="${cancelUrl}"/>
                </portlet:actionURL>
                <portlet:resourceURL id="select2-vocabulary" var="select2Url">
                    <portlet:param name="vocabulary" value="idx_level"/>
                    <portlet:param name="optgroupDisabled" value="true"/>
                </portlet:resourceURL>
                <form action="${submitUrl}" method="post">
                    <div class="form-group inline-edition">
                        <label for="${namespace}-levels">
                            <strong><op:translate key="DOCUMENT_METADATA_LEVEL_LABEL"/></strong>
                        </label>
                        <select id="${namespace}-levels" name="inline-values" multiple="multiple"
                                class="form-control select2 select2-inline-edition" data-url="${select2Url}" data-no-results="${noResults}" data-searching="${searching}"
                                data-placeholder="${placeholder}">
                            <c:forEach var="level" items="${levels}">
                                <option value="${level}" selected="selected"><ttc:vocabularyLabel name="idx_level"
                                                                                                  key="${level}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="submit" class="d-none">
                </form>
            </c:otherwise>
        </c:choose>

        <%--Subjects--%>
        <c:set var="subjects" value="${document.properties['idxcl:subjects']}"/>
        <c:choose>
            <c:when test="${readOnly}">
                <p class="mb-2">
                    <strong><op:translate key="DOCUMENT_METADATA_SUBJECT_LABEL"/></strong>
                </p>
                <ul class="list-inline">
                    <c:forEach var="subject" items="${subjects}">
                        <li class="list-inline-item">
                        <span class="badge badge-pill badge-orange-dark"><ttc:vocabularyLabel name="idx_subject"
                                                                                              key="${subject}"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>

            <c:otherwise>
                <c:set var="placeholder"><op:translate key="DOCUMENT_METADATA_SUBJECT_PLACEHOLDER"/></c:set>
                <portlet:actionURL name="inline-edition" var="submitUrl">
                    <portlet:param name="property" value="idxcl:subjects"/>
                    <portlet:param name="cancel-url" value="${cancelUrl}"/>
                </portlet:actionURL>
                <portlet:resourceURL id="select2-vocabulary" var="select2Url">
                    <portlet:param name="vocabulary" value="idx_subject"/>
                    <portlet:param name="optgroupDisabled" value="true"/>
                </portlet:resourceURL>
                <form action="${submitUrl}" method="post">
                    <div class="form-group inline-edition">
                        <label for="${namespace}-subjects">
                            <strong><op:translate key="DOCUMENT_METADATA_SUBJECT_LABEL"/></strong>
                        </label>
                        <select id="${namespace}-subjects" name="inline-values" multiple="multiple"
                                class="form-control select2 select2-inline-edition" data-url="${select2Url}" data-no-results="${noResults}" data-searching="${searching}"
                                data-placeholder="${placeholder}">
                            <c:forEach var="subject" items="${subjects}">
                                <option value="${subject}" selected="selected"><ttc:vocabularyLabel
                                        name="idx_subject"
                                        key="${subject}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="submit" class="d-none">
                </form>
            </c:otherwise>
        </c:choose>

        <%--Document types--%>
        <c:set var="documentTypes" value="${document.properties['idxcl:documentTypes']}"/>
        <c:choose>
            <c:when test="${readOnly}">
                <p class="mb-2">
                    <strong><op:translate key="DOCUMENT_METADATA_DOCUMENT_TYPE_LABEL"/></strong>
                </p>
                <ul class="list-inline">
                    <c:forEach var="documentType" items="${documentTypes}">
                        <li class="list-inline-item">
                        <span class="badge badge-pill badge-orange-dark"><ttc:vocabularyLabel name="idx_document_type"
                                                                                              key="${documentType}"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>

            <c:otherwise>
                <c:set var="placeholder"><op:translate key="DOCUMENT_METADATA_DOCUMENT_TYPE_PLACEHOLDER"/></c:set>
                <portlet:actionURL name="inline-edition" var="submitUrl">
                    <portlet:param name="property" value="idxcl:documentTypes"/>
                    <portlet:param name="cancel-url" value="${cancelUrl}"/>
                </portlet:actionURL>
                <portlet:resourceURL id="select2-vocabulary" var="select2Url">
                    <portlet:param name="vocabulary" value="idx_document_type"/>
                    <portlet:param name="optgroupDisabled" value="true"/>
                </portlet:resourceURL>
                <form action="${submitUrl}" method="post">
                    <div class="form-group inline-edition">
                        <label for="${namespace}-document-types">
                            <strong><op:translate key="DOCUMENT_METADATA_DOCUMENT_TYPE_LABEL"/></strong>
                        </label>
                        <select id="${namespace}-document-types" name="inline-values" multiple="multiple"
                                class="form-control select2 select2-inline-edition" data-url="${select2Url}" data-no-results="${noResults}" data-searching="${searching}"
                                data-placeholder="${placeholder}">
                            <c:forEach var="documentType" items="${documentTypes}">
                                <option value="${documentType}" selected="selected"><ttc:vocabularyLabel
                                        name="idx_document_type" key="${documentType}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="submit" class="d-none">
                </form>
            </c:otherwise>
        </c:choose>
    </div>


	<c:set var="enabled" value="${document.properties['rshr:enabledLink']}" />
	<c:set var="targets" value="${document.properties['rshr:targets']}" />


	<c:if test="${not readOnly and ((not empty targets) or (enabled)) }">
        <div class="card card-custom mb-3">
            <div class="card-body p-3">
                <c:set var="share" value="${document.properties['rshr:linkId']}"/>

                <c:if test="${not empty targets}">
                    <div class="d-flex align-items-center">
                        <span class="badge badge-pill badge-green-pronote flex-shrink-0 mr-2">${fn:length(targets)}</span>
                        <a href="javascript:" class="text-black no-ajax-link " data-toggle="modal"
                           data-target="#${namespace}-targets">
                            <strong>
                                <c:choose>
                                    <c:when test="${fn:length(targets) eq 1}"><op:translate
                                            key="SHARED_TARGET_REFERENCE"/></c:when>
                                    <c:otherwise><op:translate key="SHARED_TARGET_REFERENCES"/></c:otherwise>
                                </c:choose>
                            </strong>
                        </a>
                    </div>

                    <%--Target modal--%>
                    <div id="${namespace}-targets" class="modal fade" tabindex="-1" role="dialog">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">
                                        <span><op:translate key="SHARED_TARGET_TITLE"/></span>
                                    </h5>

                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>

                                <div class="modal-body">
                                    <table class="table-responsive">
                                        <thead class="thead-light">
                                        <tr>
                                            <th class="w-25"><op:translate
                                                    key="SHARED_TARGET_REFERENCES_ORGANIZATION"/></th>
                                            <th class="w-25"><op:translate key="SHARED_TARGET_REFERENCES_DATE"/></th>
                                            <th class="w-25"><op:translate key="SHARED_TARGET_REFERENCES_GROUP"/></th>
                                            <th class="w-25"><op:translate key="SHARED_TARGET_REFERENCES_CONTEXT"/></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="target" items="${targets}">
                                            <c:set var="pubOrganization" value="${target.pubOrganization}"/>
                                            <c:set var="pubDate" value="${target.pubDate}"/>
                                            <c:set var="pubGroups" value="${target.pubGroups}"/>
                                            <c:set var="pubContext" value="${target.pubContext}"/>

                                            <tr>
                                                <td class="align-top"><c:if test="${not empty pubOrganization}">
                                                
                                                    TODO ETABLISSEMENT
                                                </c:if></td>
                                                <td class="align-top"><c:if test="${not empty pubDate}">
                                                    <fmt:formatDate value="${pubDate}" type="date" dateStyle="long"/>
                                                </c:if></td>
                                                <td class="align-top"><c:if test="${not empty pubGroups}">
                                                    <ul class="list-inline">
                                                        <c:forEach var="group" items="${pubGroups}">
                                                            <li class="list-inline-item">${group}</li>
                                                        </c:forEach>
                                                    </ul>
                                                    ${target.pubGroup}
                                                </c:if></td>
                                                <td class="align-top"><c:if test="${not empty pubContext}">
                                                    <ttc:vocabularyLabel name="idx_pub_context"
                                                                         key="${target.pubContext}"/>
                                                </c:if></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>


                <c:choose>
                    <c:when test="${enabled}">
                        <div class="d-flex flex-wrap flex-column my-1">
                            <div>
	                            <a href="/s/${share}"
	                               class="btn btn-link btn-link-hover-green btn-sm text-secondary text-truncate">
	                                <i class="glyphicons glyphicons-basic-paired"></i>
	                                <span><op:translate key="SHARED_LINK"/></span>
	                            </a>
                            </div>
                            
		                    
		                    <div class="m-2 d-flex flex-grow-1 flex-row"> 
                                <div  class="text-truncate flex-grow-1 flex-shrink-1 d-flex" >
                                    <input id="${namespace}-link" readonly class="form-control form-control-sm align-self-center flex-grow-1" value="${baseUrl}/s/${share}">
                                </div>
                                
                                <button type="button" class="btn-sm ml-1 p-0 btn-secondary pull-right" data-clipboard-target="#${namespace}-link" data-clipboard-message-success="<op:translate key="DOCUMENT_METADATA_SHARE_LINK_COPIED" />" title="<op:translate key="DOCUMENT_METADATA_COPY_SHARE_LINK" />">
                                        <i class="halflings halflings-copy"></i>
                                </button>

                            </div>
                           
                            <div>
	                            <c:set var="targets" value="${document.properties['rshr:targets']}"/>    
					            <c:choose>
					                <c:when test="${not empty targets}">       
					                    <a title="${title}" class="btn btn-link btn-link-hover-green btn-sm text-secondary text-truncate  no-ajax-link" href="#deactivateShareLinkModalId" data-toggle="modal">
					                        <i class="glyphicons glyphicons-basic-paired-off"></i>
					                        <span><op:translate key="SHARED_LINK_DEACTIVATE"/></span>
					                    </a>
					                 </c:when>
					                <c:otherwise>
			                            <portlet:actionURL name="link-activation" var="deactivationUrl">
			                                <portlet:param name="activate" value="false"/>
			                            </portlet:actionURL>
			                            <a href="${deactivationUrl}"
			                               class="btn btn-link btn-link-hover-green btn-sm text-secondary text-truncate">
			                                <i class="glyphicons glyphicons-basic-paired-off"></i>
			                                <span><op:translate key="SHARED_LINK_DEACTIVATE"/></span>
			                            </a>
					                 </c:otherwise>          
					            </c:choose>
				            </div>
				            
				            
				            
				            
				            
	                       <%--Format--%>
	                        <c:if test="${document.pdfConvertible}">
	                            <portlet:actionURL name="inline-edition" var="submitUrl">
	                                <portlet:param name="property" value="rshr:format"/>
	                                <portlet:param name="cancel-url" value="${cancelUrl}"/>
	                            </portlet:actionURL>
	                            <form action="${submitUrl}" method="post" class="mt-3">
	                                <c:if test="${not empty targets}">
	                                    <c:set var="value"><op:translate key="DOCUMENT_CHANGE_FORMAT_WARN_MESSAGE"/></c:set>
	                                    <input type="hidden" name="warn-message" value="${value}">
	                                </c:if>
	
	                                <div class="form-group inline-edition">
	                                    <p class="mb-2">
	                                        <strong><op:translate key="DOCUMENT_METADATA_FORMAT_LABEL"/></strong>
	                                    </p>
	
	                                    <div class="form-check form-check-inline">
	                                        <input id="${namespace}-format-pdf" type="radio" class="form-check-input"
	                                               name="inline-values"
	                                               value="pdf" ${empty document.properties['rshr:format'] or document.properties['rshr:format'] eq 'pdf' ? 'checked' : ''}>
	                                        <label for="${namespace}-format-pdf" class="form-check-label"><op:translate
	                                                key="SHARED_FORMAT_PDF"/></label>
	                                    </div>
	                                    <div class="form-check form-check-inline">
	                                        <input id="${namespace}-format-native" type="radio" class="form-check-input"
	                                               name="inline-values"
	                                               value="native" ${document.properties['rshr:format'] eq 'native' ? 'checked' : ''}>
	                                        <label for="${namespace}-format-native" class="form-check-label"><op:translate
	                                                key="SHARED_FORMAT_NATIVE"/></label>
	                                    </div>
	                                </div>
	
	                                <input type="submit" class="d-none">
	                            </form>
	                        </c:if>				            
                             
                        </div>

                       
                    </c:when>
                </c:choose>
            </div>
        </div>
    </c:if>


    <%--Mutualized informations--%>
    <c:if test="${not readOnly and not empty document.publishedDocuments}">
        <div class="card card-custom card-custom-border-left card-custom-orange mb-3">
            <div class="card-body p-3">
                <div class="d-flex">
                
                    <c:if test="${not empty publicationDocument}">
 	                    <div class="flex-shrink-0 mr-3">
	                            <%--Icon--%>
	                        <div class="card-custom-icon">
	                            <ttc:icon document="${publicationDocument}"/>
	                        </div>
	                    </div>
                    </c:if>

                    <div class="flex-grow-1 flex-shrink-1">
                            <%--Title--%>
                        <div class="mb-1">
                            <small class="text-secondary"><op:translate
                                    key="DOCUMENT_MUTUALIZATION_TITLE"/></small>
                            <strong>${document.properties['mtz:title']}</strong>
                        </div>

                            <%--Format--%>
                        <c:if test="${not empty publicationFormat}">
	                        <div class="mb-1">
	                            <small class="text-secondary"><op:translate
	                                    key="DOCUMENT_MUTUALIZATION_FORMAT"/></small>
	                            <strong>${publicationFormat}<strong>
	                        </div>
	                    </c:if>

                            <%--Keywords--%>
                        <div class="mb-3">
                            <small class="text-secondary"><op:translate
                                    key="DOCUMENT_MUTUALIZATION_KEYWORDS"/></small>
                            <c:choose>
                                <c:when test="${empty keywords}">
                                    <span>&ndash;</span>
                                </c:when>

                                <c:otherwise>
                                    <strong>${keywords}</strong>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <ul class="d-flex flex-wrap list-inline text-secondary">
                                <%--Views--%>
                            <li class="list-inline-item text-truncate">
                                <i class="glyphicons glyphicons-basic-eye"></i>
                                <strong class="text-black">${document.properties['mtz:views']}</strong>
                                <c:choose>
                                    <c:when test="${empty document.properties['mtz:views'] or document.properties['mtz:views'] le 1}">
                                        <span><op:translate key="DOCUMENT_MUTUALIZATION_VIEW"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span><op:translate key="DOCUMENT_MUTUALIZATION_VIEWS"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </li>

                                <%--Downloads--%>
                            <li class="list-inline-item text-truncate">
                                <i class="glyphicons glyphicons-basic-save"></i>
                                <strong class="text-black">${document.properties['mtz:downloads']}</strong>
                                <c:choose>
                                    <c:when test="${empty document.properties['mtz:downloads'] or document.properties['mtz:downloads'] le 1}">
                                        <span><op:translate key="DOCUMENT_MUTUALIZATION_DOWNLOAD"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span><op:translate key="DOCUMENT_MUTUALIZATION_DOWNLOADS"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </ul>

                        <c:if test="${document.properties['mtz:downloads'] ge 1}">
	                        <div>
	                            <c:set var="discussionUrl"><ttc:discussion publicationId="${document.properties['ttc:webid']}"/></c:set>
	                            <a href="${discussionUrl}" class="btn btn-link btn-link-hover-orange btn-sm text-orange-dark no-ajax-link">
	                                <i class="glyphicons glyphicons-basic-refresh"></i>
	                                <span><op:translate key="DOCUMENT_MUTUALIZATION_CONTACT_READERS"/></span>
	                            </a>
	                        </div>
                        </c:if>	                        

                            <%--Desynchronized indicator--%>
                        <c:if test="${desynchronized}">
                            <p class="text-muted mt-3 mb-0">
                                <small><op:translate key="DOCUMENT_MUTUALIZATION_DESYNCHRONIZED"/></small>
                            </p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </c:if>


    <c:if test="${not readOnly and not empty document.properties['mtz:sourceWebId']}">
        <div class="card card-custom card-custom-border-left card-custom-orange mb-3">
            <div class="card-body py-3">
                <p class="card-text"><op:translate key="DOCUMENT_COPIED_INFORMATION"/></p>

                <c:choose>
                    <c:when test="${empty source}">
                        <p class="card-text text-muted"><op:translate
                                key="DOCUMENT_COPIED_SOURCE_NOT_FOUND"/></p>
                    </c:when>

                    <c:otherwise>
                        <c:set var="discussionUrl"><ttc:discussion
                                participant="${source.properties['dc:creator']}"
                                publicationId="${document.properties['mtz:sourceWebId']}"/></c:set>
                        <p class="card-text">
                            <a href="${discussionUrl}" class="btn btn-link btn-link-hover-orange btn-sm text-orange-dark">
                                <span><op:translate key="DOCUMENT_COPIED_CONTACT_AUTHOR"/></span>
                            </a>
                        </p>

                        <ttc:documentLink document="${source}" var="sourceLink" permalink="true"/>
                        <p class="card-text">
                            <a href="${sourceLink.url}" class="btn btn-link btn-link-hover-orange btn-sm text-orange-dark no-ajax-link">
                                <span><op:translate key="DOCUMENT_COPIED_VIEW_SOURCE"/></span>
                            </a>
                        </p>

                        <%--Desynchronized indicator--%>
                        <c:if test="${desynchronizedFromSource}">
                            <p class="card-text text-muted">
                                <small><op:translate key="DOCUMENT_COPIED_NEW_VERSION_FOUND"/></small>
                            </p>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>

    <c:if test="${readOnly and contactAuthor}">
        <div class="card card-custom card-custom-border-left card-custom-orange mb-3">
            <div class="card-body py-3">
 
                        <c:set var="discussionUrl"><ttc:discussion
                                participant="${document.properties['dc:lastContributor']}"
                                publicationId="${document.properties['ttc:webid']}"/></c:set>
                        <p class="card-text">
                            <a href="${discussionUrl}" class="btn btn-link btn-link-hover-orange btn-sm text-orange-dark">
                                <span><op:translate key="DOCUMENT_COPIED_CONTACT_AUTHOR"/></span>
                            </a>
                        </p>
                    

            </div>
        </div>
    </c:if>
    
    
  
</div>
