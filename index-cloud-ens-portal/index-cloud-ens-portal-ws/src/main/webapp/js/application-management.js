$JQry(function() {
	var timer, xhr;
	
	$JQry(".application-management input[name=filter]").keyup(function(event) {
		// Clear timer
		clearTimeout(timer);
		// Abort previous Ajax request
		if (xhr !== undefined) {
			xhr.abort();
		}
		
		timer = setTimeout(function() {
			var $target = $JQry(event.target),
				$form = $target.closest("form"),
				$results = $form.find(".form-group.results");
			
			xhr = jQuery.ajax({
				url: $form.data("url"),
				async: true,
				cache: true,
				headers: {
					"Cache-Control": "max-age=60, public"
				},
				data: {
					filters: $form.serialize()
				},
				dataType: "html",
				success : function(data, status, xhr) {
					$results.html(data);
					
					$JQry(".application-management a").click(selectApplication)
				}
			});
		}, 200);
	});

	$JQry(".application-management a").click(selectApplication);
	
});


/**
 * Select application.
 * 
 * @param event click event
 */
function selectApplication(event) {
	var $target = $JQry(event.target),
		$link = $target.closest("a"),
		id = $link.data("id"),
		$form = $target.closest("form"),
		$selected = $form.find("input[type=hidden][name=selectedApplicationId]"),
		$submit = $form.find("input[type=submit]");
	
	$selected.val(id);
	
	$submit.click();
}
