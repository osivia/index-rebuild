$JQry(function() {

    $JQry("select.select2.select2-inline-edition").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var url = $element.data("url");
            var options = {
                closeOnSelect : true,
                containerCssClass : "select2-inline-edition-container",
                dropdownCssClass : "select2-inline-edition-dropdown",
                theme : "bootstrap4",
                width : "100%"
            };
            
            var tags = false;
            
            
            if( $element.attr('data-tags') == "true")	{
            	tags = true;
            }
            
            if( tags == true)	{

            	options["tokenSeparators"] = [','];
            }

            if (url !== undefined) {
                options["ajax"] = {
                    url : adaptAjaxRedirection(url),
                    dataType : "json",
                    delay : 250,
                    data : function(params) {
                        return {
                            filter : params.term,
                        };
                    },
                    processResults : function(data, params) {
                        return {
                            results : data
                        };
                    },
                    cache : true,
                    
                     
                    transport: function (params, success, failure) {
                        var $request = $JQry.ajax(params);

                        $request.then(success);
                        $request.defaultFailure=failure;
                        $request.fail(select2LoadFailure);

                        return $request;
                      }
                };

                options["escapeMarkup"] = function(markup) {
                    return markup;
                };

                options["templateResult"] = function(params) {
                    var $result = $JQry(document.createElement("span"));

                    if (params.loading) {
                        $result.text(params.text);
                    } else {
                        $result.text(params.text);
                        if (params.level !== undefined) {
                            $result.addClass("level-" + params.level);
                        }
                        if (params.optgroup) {
                            $result.addClass("optgroup");
                        }
                    }

                    return $result;
                };

                options["templateSelection"] = function (params) {
                    var $result = $JQry(document.createElement("span"));
                    $result.addClass("text-truncate");
                    $result.text(params.text);

                    return $result;
                };
            }

            
            // Internationalization
            options["language"] = {};
            if ($element.data("input-too-short") !== undefined) {
                options["language"]["inputTooShort"] = function () {
                    return $element.data("input-too-short");
                }
            }
            if ($element.data("error-loading") !== undefined) {
                options["language"]["errorLoading"] = function () {
                    return $element.data("error-loading");
                }
            }
            if ($element.data("loading-more") !== undefined) {
                options["language"]["loadingMore"] = function () {
                    return $element.data("loading-more");
                }
            }
            if ($element.data("searching") !== undefined) {
                options["language"]["searching"] = function () {
                    return $element.data("searching");
                }
            }
            if ($element.data("no-results") !== undefined) {
                options["language"]["noResults"] = function () {
                    return $element.data("no-results");
                }
            }

            
            $element.select2(options);
            

            // Close on unselect
            $element.on("select2:unselect", function (event) {
                setTimeout(function() {
                    var search = $element.siblings().find(".select2-search__field");
                    search.val("");
                    $element.select2("close");
                }, 100);
            });
          

            
            $element.change(function(event) {
                var $form = $element.closest("form");
                var $submit = $form.find("button[type=submit], input[type=submit]");

                $submit.click();
            });


            $element.data("loaded", true);
        }
    });


    $JQry(".inline-edition textarea, .inline-edition input").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var timer;

            $element.change(function(event) {
            	
                var $target = $JQry(event.target);
                if( $target.hasClass('select2-search__field') == false)	{
            	
	                // Clear timer
	                clearTimeout(timer);
	
	                var $target = $JQry(event.target);
	                var $form = $target.closest("form");
	                var $submit = $form.find("button[type=submit], input[type=submit]");
	
	                $submit.click();
                }
            });

            $element.keyup(function(event) {
            	
                var $target = $JQry(event.target);
                if( $target.hasClass('select2-search__field') == false)	{
	            	
	                // Clear timer
	                clearTimeout(timer);
	
	                timer = setTimeout(function() {
	                    var $target = $JQry(event.target);
	                    var $form = $target.closest("form");
	                    var $submit = $form.find("button[type=submit], input[type=submit]");
	
	                    $submit.click();
	                }, 500);
                }
            });

            $element.data("loaded", true);
        }
    });


    $JQry("[contenteditable=true]").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {
            var timer;

            $element.keyup(function(event) {
                // Clear timer
                clearTimeout(timer);

                timer = setTimeout(function() {
                    var $target = $JQry(event.target);
                    var $form = $target.closest("form");
                    var $input = $form.find("input[type=hidden][name='inline-values']");
                    var $submit = $form.find("input[type=submit]");

                    $input.val($target.get(0).innerText);
                    $submit.click();
                }, 500);
            });


            $element.data("loaded", true);
        }
    });
    
    
    
    $JQry("#logout").each(function(index, element) {

		const queryString = window.location.search;
		const urlParams = new URLSearchParams(queryString);
		const redirection = urlParams.get('redirection');

		
		if( redirection != null)	{
			// Consider portal redirection as an ordinary app
			var $disconnection = $JQry("#disconnection");
			console.log( "redirection " +$disconnection.data("redirection"));
			console.log( "redirection " +$disconnection.data("apps") );
			$disconnection.data("apps", $disconnection.data("apps") + "|" + $disconnection.data("redirection"));
			$disconnection.data("redirection", redirection);
		}
		
		
		logout();
		

	});
    
    
    var passwordRulesInformationTimer;
    var passwordRulesInformationXhr;


    $JQry(".password-control input[data-password-control-url]").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("password-loaded")) {

        	var $controlPasswrod = $JQry(".password-control");
        	
        	// Initialisation
            passwordRulesInformationTimer = setTimeout(function()	{
            	updatePasswordRulesInformation( true);
            }, 100);

            $element.on("input", function(event) {
                // Clear timer
                clearTimeout(passwordRulesInformationTimer);

                passwordRulesInformationTimer = setTimeout(function()	{
                	updatePasswordRulesInformation( false);
                }, 100);
            });
            
            
           // popover
            $controlPasswrod.find("[data-password-control-url]").popover({
    			content: function () {
    				var result = $element.data("popover-content");
    				return result;
    			},
    			html: true,
    			placement: function () {
    					return "auto";
    			},
    			trigger: "focus"
    		});

            $element.data("password-loaded", true);
        }
    });


    function updatePasswordRulesInformation( initialization) {
        var $controlPasswrod = $JQry(".password-control");
        var $placeholder = $controlPasswrod.find("[data-password-control-url]");
        var $input = $JQry(".password-control input[data-password-control-url]");

        // Abort previous AJAX request
        if (passwordRulesInformationXhr && passwordRulesInformationXhr.readyState !== 4) {
            passwordRulesInformationXhr.abort();
        }

        passwordRulesInformationXhr = jQuery.ajax({
            url: $placeholder.data("password-control-url"),
            type: "POST",
            async: true,
            cache: false,
            data: {
                password: $input.val()
            },
            dataType: "html",
            success : function(data, status, xhr) {
            	$placeholder.data("popover-content", data);
            	if( initialization == false)
            		$placeholder.popover('show');
             }
        });
    }
    

});


function select2LoadFailure( request, errorType ) {
	
	var defaultFailure = true
	
	if( errorType == 'parsererror')	{
		if( handleAjaxRedirection(request.responseText))	{
				defaultFailure = false;
		}
	}
	
	if( defaultFailure == true)
		request.defaultFailure();
           
}

function setDocHeight() {
	document.documentElement.style.setProperty('--vh', `${window.innerHeight/100}px`);
	};

setDocHeight();
addEventListener('resize', setDocHeight)
addEventListener('orientationchange', setDocHeight)	

// End of qcm
function onFinish() {
	var element = document.getElementById('qcmPlayer')
	if (element != null) {
		element.contentWindow.location.reload();
	}	
}




/* 
 * Restore cursor positions on file browser 
 * 
 * (back button)
 */


function readCookie(name) {
    // Escape regexp special characters (thanks kangax!)
    name = name.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');

    var regex = new RegExp('(?:^|;)\\s?' + name + '=(.*?)(?:;|$)','i'),
    
    match = document.cookie.match(regex);

    return match && unescape(match[1]); 
}

//Get unique key per session/location
function getScrollKey() {
	var session = readCookie("PORTALSESSIONID");
	if( session == null)
		session = "";
	key = session + "/scroll/" +  window.location.href;
	return key;
}

$JQry(document).ready(function(){
	var key = getScrollKey();
	if (sessionStorage.getItem(key) != undefined)	{
		filler = $JQry(".portlet-filler").first();
		if( filler != undefined)	{
			filler.scrollTop(sessionStorage.getItem(key));
			sessionStorage.removeItem(key)
		}
	}
});

var isOnIOS = navigator.userAgent.match(/iPad/i)|| navigator.userAgent.match(/iPhone/i);
var eventName = isOnIOS ? "pagehide" : "beforeunload";


addEventListener(eventName, function (event) {
	filler = $JQry(".portlet-filler").first();
	// Should work in any case
	// But validate on filebrowser first
	if (filler.parents('.file-browser').length) {
		if( filler != undefined)	{
			var key = getScrollKey();			
			sessionStorage.setItem(key, filler.scrollTop());
		}	
	}	
});

