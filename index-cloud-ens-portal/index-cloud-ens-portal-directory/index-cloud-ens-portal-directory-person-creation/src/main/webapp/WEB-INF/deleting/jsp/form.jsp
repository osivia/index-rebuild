<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="check" var="checkUrl"/>
<portlet:actionURL name="cancel" var="cancelUrl"/>
<portlet:actionURL name="delete" var="deleteUrl"/>

<c:set var="namespace"><portlet:namespace/></c:set>


<div class="delete-account" data-validated="${form.validated}" data-delete-url="${form.validated ? deleteUrl : ''}">
    <div class="portlet-filler d-flex flex-column flex-grow-1">
        <div class="row">
            <div class="offset-md-1 col-md-10 offset-lg-2 col-lg-8">
                <div class="card border-danger">
                    <div class="card-body">
                        <h3 class="card-title h5 text-danger"><op:translate key="deleting.title"/></h3>

                        <p class="card-text text-danger">
                            <span><op:translate key="deleting.alert"/></span>
                        </p>

                        <form:form action="${checkUrl}" method="post" modelAttribute="form">
                            <div class="form-group required">
                                <form:label path="password"><op:translate key="deleting.password.label"/></form:label>
                                <c:set var="placeholder"><op:translate key="deleting.password.placeholder"/></c:set>
                                <form:password path="password" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="${placeholder}" autocomplete="false"/>
                                <small class="form-text text-muted"><op:translate key="deleting.password.help"/></small>
                                <form:errors path="password" cssClass="invalid-feedback" />
                            </div>

                            <div class="form-group">
                                <div class="form-check">
                                    <form:checkbox id="${namespace}-accepted" path="accepted" cssClass="form-check-input" cssErrorClass="form-check-input is-invalid" />
                                    <form:label for="${namespace}-accepted" path="accepted" cssClass="form-check-label"><op:translate key="deleting.accepted.label"/></form:label>
                                    <form:errors path="accepted" cssClass="invalid-feedback" />
                                </div>
                            </div>

                            <div class="text-right">
                                <a href="${cancelUrl}" class="btn btn-secondary">
                                    <span><op:translate key="deleting.cancel"/></span>
                                </a>

                                <button type="submit" class="btn btn-danger ml-2">
                                    <span><op:translate key="deleting.submit"/></span>
                                </button>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
