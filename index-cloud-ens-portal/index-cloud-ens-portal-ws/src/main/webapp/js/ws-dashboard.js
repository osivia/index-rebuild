
function openDashboardModal(idModal,revokeUrl)	{
	var $modal=$JQry("#"+idModal );

	$target = $modal.find(".revoke-target");
	$target.attr("href", revokeUrl);
	
	$modal.modal();
	
}

