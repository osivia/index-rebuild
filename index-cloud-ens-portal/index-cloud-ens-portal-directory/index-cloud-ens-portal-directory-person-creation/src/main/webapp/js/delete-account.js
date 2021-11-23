$JQry(function() {
	$JQry(".delete-account[data-validated][data-delete-url]").each(function(index, element) {
		var $element = $JQry(element);

		if ($element.data("validated")) {
			
			var headers = getHeaders();
			
			jQuery.ajax({
				url: $element.data("delete-url"),
				type: "POST",
				async: false,
				cache: false,
				headers: headers,
				success: function() {
					logout();
				}
			});
		}
	});
});
