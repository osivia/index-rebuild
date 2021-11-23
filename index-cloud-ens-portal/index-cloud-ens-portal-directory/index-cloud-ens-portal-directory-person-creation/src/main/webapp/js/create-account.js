$JQry(function() {
    var passwordRulesInformationTimer;
    var passwordRulesInformationXhr;


    $JQry(".password-control input[data-password-control-url]").each(function(index, element) {
        var $element = $JQry(element);

        if (!$element.data("loaded")) {

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
    				if( window.innerWidth < 768)
    					return "auto";
    				else
    					return "left";
    			},
    			trigger: "focus"
    		});

            $element.data("loaded", true);
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










