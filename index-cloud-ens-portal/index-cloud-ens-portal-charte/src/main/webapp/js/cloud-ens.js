

$JQry(function() {

   
    
    
    $JQry("#logout").each(function(index, element) {

		const queryString = window.location.search;
		const urlParams = new URLSearchParams(queryString);
		const redirection = urlParams.get('redirection');

		
		if( redirection != null)	{
			// Consider portal redirection as an ordinary app
			var $disconnection = $JQry("#disconnection");
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









