$JQry(function () {
    var $upload = $JQry(".file-browser-upload");

    if (!$upload.data("loaded")) {
        // File upload
        $upload.fileupload({
            dropZone: ".file-browser-drop-zone",
            singleFileUploads: true,

            add: function (event, data) {
                var $target = $JQry(event.target);
                var $files = $target.find(".file-browser-upload-files");
                var $browser = $target.closest(".file-browser");

                $target.removeClass("d-none");

                data.context = $JQry("<li></li>").addClass("list-group-item").appendTo($files);

                $JQry.each(data.files, function (index, file) {
                    var $items = $browser.find("[data-text=\"" + file.name + "\"]");

                    // File name
                    $JQry("<p></p>").addClass("mb-2").text(file.name).appendTo(data.context);

                    // Row
                    var $row = $JQry("<div></div>").addClass("row no-gutters align-items-center").appendTo(data.context);

                    // Progress
                    $JQry("<div></div>").addClass("col").append($JQry("<div></div>").addClass("progress").append($JQry("<div></div>").addClass("progress-bar"))).appendTo($row);

                    if (file.size > $target.data("max-file-size")) {
                        // Error
                        $JQry("<span></span>").addClass("badge badge-danger ml-2").text($target.data("error-size")).appendTo(data.context.find("p").first());
                        file.error = true;
                    } else if ($items.length) {
                        // Warning
                        if ($items.data("pronote")) {
                            $JQry("<span></span>").addClass("badge badge-danger ml-2").text($target.data("warning-replace-pronote")).appendTo(data.context.find("p").first());
                        } else {
                            $JQry("<span></span>").addClass("badge badge-warning ml-2").text($target.data("warning-replace")).appendTo(data.context.find("p").first());
                        }
                        file.warning = true;

                        // Buttons
                        var $buttons = $JQry("<div></div>").addClass("col-auto").appendTo($row);
                        // Start button
                        var $start = $JQry("<button></button>").attr("type", "button").addClass("btn btn-outline-secondary btn-sm ml-2 start").text($target.data("label-start")).appendTo($buttons);
                        $start.click(function () {
                            file.warning = false;
                            data.submit(event);
                        });
                        // Cancel button
                        var $cancel = $JQry("<button></button>").attr("type", "button").addClass("btn btn-outline-secondary btn-sm ml-2 cancel").text($target.data("label-cancel")).appendTo($buttons);
                        $cancel.click(function () {
                            data.abort();

                            var $target = $JQry(event.target);

                            if (!$target.find("li").length) {
                                $target.addClass("d-none");

                                if( $target.data("upload-ended") === true)	{
                                	// Refresh
                                	updatePortletContent(this, $target.data("callback-url"));
                                }
                            }
                        });
                    }
                });

                data.submit();
            },

            submit: function (event, data) {
                var result = true;
                $JQry.each(data.files, function (index, file) {
                    if (file.error || file.warning) {
                        return result = false;
                    }
                });
                return result;
            },

            send: function (event, data) {
                var result = true;
                $JQry.each(data.files, function (index, file) {
                    if (file.error || file.warning) {
                        return result = false;
                    }
                });
                return result;
            },

            stop: function (event, data) {
                var $target = $JQry(event.target);

                if (!$target.find("li").length) {
                    $target.addClass("d-none");

                    $target.append('<li>Traitement des fichiers</li>');
                    // Refresh
                    updatePortletContent(this, $target.data("callback-url"));
                }
                
                $target.data("upload-ended", true);
            },

            progress: function (event, data) {
                var progress = parseInt((data.loaded / data.total) * 100, 10) + "%";
                var $bar = data.context.closest(".progress-bar");
                $bar.css("width", progress);
                $bar.text(progress);
        		if( data.loaded == data.total)	{
        			
         			
         			// Do not hide last item
        			var nbLines = 0;
        			data.context.closest("ul").children('li').each(function () {
        				var $line = $JQry(this);
        			    if( ! $line.hasClass("d-none"))	{
        			    	nbLines++;
        			    }
        			});         		
        			
         			if (nbLines > 1)	{
         				data.context.addClass("d-none");
         			}
        		}                
            }
        });


        $upload.find(".fileupload-buttonbar button.cancel").click(function (event) {
            var $target = $JQry(event.target);

            $target.closest("form").find(".file-browser-upload-files button.cancel").click();
        });


        // Drag over
        $JQry(document).bind("dragover", function (e) {
            e.preventDefault();

            var $target = $JQry(e.target);
            var $hoveredDropZone = $target.closest(".file-browser-drop-zone");
            var $dropZone = $JQry(".file-browser-drop-zone");
            var timeout = window.dropZoneTimeout;

            if (!timeout) {
                $dropZone.addClass("in");
            } else {
                clearTimeout(timeout);
            }

            if ($hoveredDropZone.length) {
                $hoveredDropZone.find(".file-browser-upload-shadowbox").addClass("bg-info");
            } else {
                $dropZone.find(".file-browser-upload-shadowbox").removeClass("bg-info");
            }

            window.dropZoneTimeout = setTimeout(function () {
                window.dropZoneTimeout = null;
                $dropZone.removeClass("in");
                $dropZone.find(".file-browser-upload-shadowbox").removeClass("bg-info");
            }, 1000);
        });


        // Drop
        $JQry(document).bind("drop", function (e) {
            var $dropZone = $JQry(".file-browser-drop-zone");

            $dropZone.removeClass("in");
            $dropZone.find(".file-browser-upload-shadowbox").removeClass("bg-info");
        });


        $upload.data("loaded", true);
    }
});
