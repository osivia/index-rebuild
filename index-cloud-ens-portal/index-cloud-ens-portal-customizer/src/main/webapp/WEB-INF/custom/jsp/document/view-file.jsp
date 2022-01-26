<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice" prefix="ttc" %>

<%@ page isELIgnored="false" %>


<c:set var="previewUrl"><ttc:filePreview document="${document}"/></c:set>
<c:set var="resourceContext"><ttc:resourceContext/></c:set>

<c:choose>
    <c:when test="${not empty QCMUrl}">
        <div class="document-qcm d-flex flex-column flex-grow-1 ">
            <iframe id="qcmPlayer" src="/index-cloud-ens-portal-charte/qcm/visioQCM.html?url=${QCMUrl}" class="flex-grow-1" onFinish="alert('on finish event!')"></iframe>
        </div>
    </c:when>

    <c:otherwise>
        <script type="text/javascript" src="/index-cloud-ens-portal-cms-nuxeo/components/PDFViewer/preview.js"></script>

        <div class="document-file d-flex flex-column flex-grow-1">
            <c:choose>
                <c:when test="${empty previewUrl}">
                    <div>
                        <div class="alert alert-warning" role="alert">
                            <span><op:translate key="DOCUMENT_FILE_NO_PREVIEW"/></span>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="d-flex flex-column flex-grow-1 justify-content-center align-items-center">
                        <div class="spinner-border mb-4" role="status" style="height: 3rem; width: 3rem;">
                            <span class="sr-only">Loading...</span>
                        </div>

                        <div class="progress w-50">
                            <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0"
                                 aria-valuemax="100"></div>
                        </div>
                    </div>

                    <%--Preview in iframe--%>
					<div class="d-lg-flex flex-column flex-grow-1 px-4 px-md-0">
						<div class="embed-responsive embed-responsive-4by3 embed-responsive-lg-0 d-lg-flex flex-column flex-grow-1">
				            <iframe src="${resourceContext}/components/PDFViewer/web/viewer.html" webkitallowfullscreen=""
				                    allowfullscreen="" class="pdf-preview-iframe d-none flex-grow-1" data-preview-url="${previewUrl}"
				                    onload="downloadPreview();"></iframe>
						</div>
					</div>

                    <div class="file-preview-unavailable d-none">
                        <div class="alert alert-warning" role="alert">
                            <span><op:translate key="DOCUMENT_FILE_PREVIEW_UNAVAILABLE"/></span>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>
