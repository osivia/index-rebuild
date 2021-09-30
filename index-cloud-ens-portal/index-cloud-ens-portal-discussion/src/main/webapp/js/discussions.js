

$JQry(function() {
    $JQry(".discussion").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var $modals = $element.find(".modal");
 
            // Update modal content
            $modals.on("show.bs.modal", function(event) {
                var $button = $JQry(event.relatedTarget);
                var $modal = $JQry(event.currentTarget);

                $modal.find("input[name=messageIndice]").val($button.data("message-id"));
                $modal.find("input[name=displayName]").val($button.data("display-name"));
            });

            // Submit modal form
            $modals.find("button[data-submit]").click(function(event) {
                var $button = $JQry(event.currentTarget);
                var $modal = $button.closest(".modal");

                // Close modal
                $modal.modal("hide");

                // Submit form
                $modal.find("input[type=submit]").click();
            });

            $element.data("loaded", true);
        }
    });


    $JQry(".discussions").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
           // Click on shadowbox
            $element.find(".discussions-shadowbox").click(function (event) {
                var $target = $JQry(event.target);
                var $selectee = $target.closest(".portal-table-selectable-filter");
                var $selectable = $selectee.closest(".portal-table-selectable");

                if (event.ctrlKey) {
                    $selectee.removeClass("ui-selected bg-primary border-primary");
                } else {
                    $selectable.find(".ui-selected").each(function (index, element) {
                        var $element = $JQry(element);

                        if (!$element.is($selectee)) {
                            $element.removeClass("ui-selected bg-primary border-primary");
                        }
                    });
                }

                // Update toolbar
                updateTableToolbar($target);
            });

            $element.data("loaded", true);
        }
    });
});
