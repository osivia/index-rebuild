

$JQry(function() {

   
    
    
    $JQry("#logout").each(function(index, element) {


		const redirection = portal_redirection;

		
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
    
    
    
    $JQry(".html-popover").each(function(index, element) {
        var $element = $JQry(element);
        
        

        if (!$element.data("html-popover")) {
        	
        	var style = $element.data("popover-style");
        	var tmpl = "<div class='popover "+style+"' role='tooltip'><div></div><h3 class='popover-header'></h3><div class='popover-body p-0'></div></div>";
        	
        	
         		// popover
            	$element.popover({
        			content: function () {
        				var result = $element.data("popover-content");
        				return result;
        			},
        			title: "Creative commons",
        			html: true,
        			title: function () {
        				var title = $element.data("popover-title");
        				if( title !== undefined)
        					return title
        				else
        					return "";
        			},
        			placement: "top",
        			trigger: "manual",	
        			template: tmpl,
        			boundary: 'viewport'
        		});           	
            	
 
            	// Focus doesn't enable re-click to dismis
            	// Click mode doesn't enable to click anywhere to dismiss
            	// -> Use manual mode
            	
            	$element.on('shown.bs.popover', function() {
            		$element.data('visible', true);
            	});

            	$element.on('hidden.bs.popover', function() {
            		$element.data('visible', false);
            	});
            	
            	element.addEventListener('click', function() {
            		if( $element.data('visible') === undefined || $element.data('visible') == false)	{
            			$element.popover('show')
            		}
                }, false);
            	
            	document.addEventListener('click', function() {
           			hideAllHTMLPopovers();
                }, false);
          	
            	
            	$element.data("html-popover", true);
        }
        

        
    });
    
    

});

function hideAllHTMLPopovers(){
    $JQry('.html-popover').each(function() {
        if ($JQry(this).data("visible") == true){
        	$JQry(this).popover('hide');                
        }
    });
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









