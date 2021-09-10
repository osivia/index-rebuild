function saveSearchFiltersLocation(button) {
	var $button = $JQry(button);
	var $modalForm = $button.closest("form");
	var path = $modalForm.find("input[name$=targetPath]").val();
	var $form = $JQry(".search-filters-container form");

	// Update hidden input
	$form.find("input[type=hidden][name$=locationPath]").val(path);

	// Submit form
	$form.find("input[type=submit][name='update-location']").click();
}


function searchOptionsLoadCallback(formId) {
    var $form = $JQry("#" + formId);
    var $source = $form.find("input[type=search]");

    var $modal = $JQry("#osivia-modal");
    var $target = $modal.find("input[name=keywords]");

    $target.val($source.val());
}



$JQry(function() {
	$JQry(".search.auto-submit").each(function(index, element) {
		var $element = $JQry(element);

		if (!$element.data("loaded")) {
			var enterKey = 13;
			var timer;

			$element.find("input[name=value]").keyup(function(event) {
				// Clear timer
				clearTimeout(timer);
				
				if (event.which != enterKey) {
					timer = setTimeout(function() {
						var $target = $JQry(event.target),
							$formGroup = $target.closest(".form-group"),
							$submit = $formGroup.find("button[type=submit]");
						
						$submit.click();
					}, 200);
				}
			});
			
			$element.data("loaded", true);
		}
	});


	$JQry(".saved-searches").each(function(index, element) {
		var $element = $JQry(element);

		if (!$element.data("loaded")) {
			$element.find(".modal").on("show.bs.modal", function(event) {
				var $button = $JQry(event.relatedTarget);
				var $modal = $JQry(this);

				$modal.find("input[type=hidden][name=id]").val($button.data("id"));
			});


			$element.find(".modal button[data-submit]").click(function(event) {
				var $target = $JQry(event.target);

				$target.closest("form").find("input[type=submit]").click();
			});


			$element.data("loaded", true);
		}
	});
});

