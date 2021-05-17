var mutualizedFileBrowserLoading = false;

$JQry(function () {
    var $browser = $JQry(".file-browser");

    if (!$browser.data("mutualized-loaded")) {
        $browser.data("mutualized-loaded", true);

        var $scrollbox = $browser.find(".portlet-filler");
        var $child = $scrollbox.children();

        if ($child.height() === $scrollbox.height()) {
            loadMore();
        }

        $scrollbox.scroll(function(event) {
            if (($child.height() - $scrollbox.height() - $scrollbox.scrollTop()) < 50) {
                loadMore();
            }
        });

        $JQry(window).resize(function() {
            if ($child.height() === $scrollbox.height()) {
                loadMore();
            }
        });

        function loadMore() {
            if (!mutualizedFileBrowserLoading) {
                mutualizedFileBrowserLoading = true;

                var $more = $browser.find("[data-more]");

                if ($more.length) {
                    // Save location
                    var container = null;
                    var options = {
                        requestHeaders: ["ajax", "true", "bilto"],
                        method: "post"
                    };
                    var url = $more.data("save-position-url");
                    var eventToStop = null;
                    var callerId = null;
                    directAjaxCall(container, options, url, eventToStop, callerId);

                    // Load page
                    jQuery.ajax({
                        url: $more.data("load-page-url"),
                        async: false,
                        cache: false,
                        dataType: "html",
                        success: function (data, status, xhr) {
                            $more.closest(".portal-table-row").replaceWith(data);

                            // Update scrollbar width
                            updateTableScrollbarWidth();
                            // jQuery events
                            $JQry(document).ready();

                            mutualizedFileBrowserLoading = false;

                            if ($child.height() === $scrollbox.height()) {
                                loadMore();
                            }
                        }
                    });
                } else {
                    mutualizedFileBrowserLoading = false;
                }
            }
        }
    }
});
