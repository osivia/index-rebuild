<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects/>

<portlet:actionURL name="submitForm" var="submitFormUrl"/>

<portlet:resourceURL id="password-information" var="passwordInformationUrl"/>

<c:set var="namespace"><portlet:namespace/></c:set>


<div class="create-account">

    <div class="row d-flex justify-content-center mb-5">
        <img alt="Cloud PRONOTE" src="${renderRequest.contextPath}/img/logo-cloud-pronote.svg">
    </div>
    <div class="row">
        <div class="col-lg-8  m-auto">
            <div class="card bg-light shadow-lg cloud-ens-form">
                <div class="card-body">
                    <div class="card-title text-center font-weight-bold"><op:translate
                            key="createaccount.title"/></div>

                    <form:form action="${submitFormUrl}" method="post" modelAttribute="form" role="form">
                        <div class="row">
                            <div class="col-md">
                                    <%--Nickname--%>
                                <spring:bind path="nickname">
                                    <div class="form-group required">
                                     
                                        <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.nickname"/></c:set>
                                        <form:input path="nickname" cssClass="form-control"
                                                    cssErrorClass="form-control is-invalid" placeholder="${placeholder}" />
                                        <form:errors path="nickname" cssClass="invalid-feedback"/>
                                    </div>
                                </spring:bind>

                                    <%--Lastname--%>
                                <spring:bind path="lastname">
                                    <div class="form-group required">
                                        <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.lastname"/></c:set>                                                
                                        <form:input path="lastname" cssClass="form-control"
                                                    cssErrorClass="form-control is-invalid" placeholder="${placeholder}" />
                                        <form:errors path="lastname" cssClass="invalid-feedback"/>
                                    </div>
                                </spring:bind>

                                    <%--Firstname--%>
                                <spring:bind path="firstname">
                                    <div class="form-group required">
                                        <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.firstname"/></c:set>                                           
                                        <form:input path="firstname" cssClass="form-control"
                                                    cssErrorClass="form-control is-invalid" placeholder="${placeholder}"/>
                                        <form:errors path="firstname" cssClass="invalid-feedback"/>
                                    </div>
                                </spring:bind>
                            </div>

                            <div class="col-md">
                                    <%--Mail--%>
                                <spring:bind path="mail">
                                    <div class="form-group required">
                                        <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.mail"/></c:set>                                           
                                                
                                        <form:input path="mail" type="email" cssClass="form-control"
                                                    cssErrorClass="form-control is-invalid" placeholder="${placeholder}" />
                                        <form:errors path="mail" cssClass="invalid-feedback"/>
                                    </div>
                                </spring:bind>

                                    <%--Password--%>
                                <spring:bind path="newpassword">
                                    <div class="form-group required password-control">
                                        <div class="mb-2">
  
                                            <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.newpassword"/></c:set>                                                          
                                            <form:password path="newpassword" showPassword="true" cssClass="form-control"
                                                           cssErrorClass="form-control is-invalid" placeholder="${placeholder}" data-password-control-url="${passwordInformationUrl}"/>
                                            <form:errors path="newpassword" cssClass="invalid-feedback"/>
                                        </div>
               
                                    </div>
                                </spring:bind>

                                    <%--Password confirmation--%>
                                <spring:bind path="confirmpassword">
                                    <div class="form-group required">
                                         <c:set var = "placeholder"> <op:translate
                                                key="createaccount.form.confirmpassword"/></c:set>                                                      
                                        <form:password path="confirmpassword" showPassword="true" cssClass="form-control"
                                                       cssErrorClass="form-control is-invalid" placeholder="${placeholder}" />
                                        <form:errors path="confirmpassword" cssClass="invalid-feedback"/>
                                    </div>
                                </spring:bind>
                                

                                                                        
                            </div>
                        </div>


                        <div class="row">
                            <div class="col-md">
                                    <%--Terms of service--%>
                                <div class="form-check">
                                    <form:checkbox id="${namespace}-accept-terms-of-service" path="acceptTermsOfService"
                                                   cssClass="form-check-input"/>
                                                   
                                     <form:label for="${namespace}-accept-terms-of-service" path="acceptTermsOfService"
                                                cssClass="form-check-label">
                                            <op:translate
                                            key="createaccount.form.acceptTermsOfService.beforeLink"/>   
                                            <a href="javascript:" class="no-ajax-link" data-target="#osivia-modal"
                                                data-load-url="${termsOfServiceUrl}" data-title="${title}" data-footer="true">
                                                <span><op:translate key="createaccount.form.acceptTermsOfService.link"/></span>
                                            </a>
                                            <op:translate
                                            key="createaccount.form.acceptTermsOfService.afterLink"/> 
                                     </form:label>   
                                     
                                    
                                </div>
  
  
                                 <c:set var="termsOfServiceHasBindError">
                                    <form:errors path="acceptTermsOfService"/>
                                </c:set>
                                
                                <c:if test="${not empty termsOfServiceHasBindError}">
                                    <div class="form-group">
                                        <span class="d-block invalid-feedback"><span><op:translate key="createaccount.error.terms"/></span></span>
                                    </div>  
                                </c:if>   
                                                                 
                                 
                                
                                
                            </div>

                            <div class="col-md-auto align-self-end">
                                    <%--Buttons--%>
                                <div class="text-right mt-2">
                                    <button type="submit" name="save" class="btn btn-primary rounded-pill">
                                        <span class="text-uppercase font-weight-bold"><op:translate
                                                key="createaccount.submit"/></span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
     </div>
     <div class="row mt-3">
        <div class="col-lg-8  m-auto">
            <div class="row">
                <div class="col-auto d-flex">
                    <%--RGPD--%>
                    <div class="d-flex my-auto rgpd">
                        <img alt="RGPD" src="${renderRequest.contextPath}/img/rgpd.png">
                    </div>
                </div>
                <div class="col-md">
                    <p class="mb-1"><strong>Utiliser le Cloud PRONOTE c'est&nbsp;:</strong></p>
                    <ul class="pl-3 text-green-dark small">
                        <li class="mb-1"><strong>avoir la garantie de rester propri&eacute;taire de vos documents</strong></li>
                        <li class="mb-1"><strong>pouvoir cl&ocirc;turer votre compte &agrave; tout moment</strong></li>
                        <li class="mb-1"><strong>&ecirc;tre assur&eacute; que vos donn&eacute;es personnelles ne seront pas
                            exploit&eacute;es</strong>
                        </li>
                        <li class="mb-1"><strong>pouvoir r&eacute;cup&eacute;rer tout ou partie de vos documents quand vous le
                            souhaitez</strong>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>


