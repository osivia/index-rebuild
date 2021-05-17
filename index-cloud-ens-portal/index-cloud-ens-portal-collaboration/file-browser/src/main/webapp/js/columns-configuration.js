$JQry(function () {
    var $element = $JQry(".file-browser-columns-configuration");

    if (!$element.data("loaded")) {
        var $sortable = $element.find(".file-browser-columns-sortable");

        // Sortable
        $sortable.sortable({
            forcePlaceholderSize : true,
            handle: ".file-browser-columns-sortable-handle",
            items: "li",
            tolerance : "pointer",

            update : function(event, ui) {
                var $item = $JQry(ui.item);
                var $list = $item.closest(".file-browser-columns-sortable");
                var $form = $list.closest("form");

                // Update order
                $list.children("li").each(function(index, element) {
                    var $element = $JQry(element);
                    var $input = $element.find("input[type=hidden][name$=order]");

                    $input.val(index);
                });
            }
        });
        $sortable.disableSelection();

        $element.data("loaded", true);
    }
});
