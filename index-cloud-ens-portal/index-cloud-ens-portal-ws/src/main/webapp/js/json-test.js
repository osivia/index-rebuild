
function humanFileSize(bytes, si) {
	var thresh = si ? 1000 : 1024;
	if (Math.abs(bytes) < thresh) {
		return bytes + ' B';
	}
	var units = si ? [ 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB' ] : [
			'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB' ];
	var u = -1;
	do {
		bytes /= thresh;
		++u;
	} while (Math.abs(bytes) >= thresh && u < units.length - 1);
	return bytes.toFixed(1) + ' ' + units[u];
}

/**
 * responsible for calling the appropriate functon to make the OAuth call using
 * AJAX call
 */

var oauth = {

	/**
	 * All constants goes here
	 */
	params : {

			
	},

	/**
	 * Fetch out the token
	 */
	getToken : function() {
		if (this.params.token) {
			return this.params.token;
		} else {
			return false;
		}
	},

	/**
	 * set the token
	 */
	authenticate : function( username, password) {
		var deferred = jQuery
				.ajax({
					url : this.params.accessUrl,
					method : 'POST',
					dataType : 'text',
					data : {
						grant_type: 'password',
						username : username,
						password: password
					},
				
					headers : {
						'Accept' : 'application/json, application/x-www-form-urlencoded',
						'Content-Type' : 'application/x-www-form-urlencoded',
						'Authorization' : 'Basic '+ btoa( this.params.clientId + ":" + this.params.clientSecretId)
					},
					complete : function(xhr, data) {
						console.warn(data);
						// called when complete
					}
				});
		return deferred.promise();
	},
	
	
	refresh : function()	{
		var deferred = jQuery
		.ajax({
			url : this.params.accessUrl,
			method : 'POST',
			dataType : 'text',
			data : {
				grant_type: 'refresh_token',
				refresh_token: this.params.refreshToken,
	
			},
		
			headers : {
				'Accept' : 'application/json, application/x-www-form-urlencoded',
				'Content-Type' : 'application/x-www-form-urlencoded',
				'Authorization' : 'Basic '+ btoa( this.params.clientId + ":" + this.params.clientSecretId)
			},
			complete : function(xhr, data) {
				console.warn(data);
				// called when complete
			}
		});
		return deferred.promise();		
	},
	
	
	grant : function( code)	{
		
		console.debug('grant' + code);
		
		var deferred = jQuery
		.ajax({
			url : this.params.accessUrl,
			method : 'POST',
			dataType : 'text',
			data : {
				grant_type: 'authorization_code',
				code: code,
// Comment to enable header authorisation 				
				client_id: this.params.clientId,
				client_secret: this.params.clientSecretId,
// End comment				
				redirect_uri: oauth.params.clientUrl					
			},
		
			headers : {
				'Accept' : 'application/json, application/x-www-form-urlencoded',
				'Content-Type' : 'application/x-www-form-urlencoded',
// Uncomment to enable header authorisation 
//				'Authorization' : 'Basic '+ btoa( this.params.clientId + ":" + this.params.clientSecretId)
			},
			complete : function(xhr, data) {
				console.warn(data);
				// called when complete
			}
		});
		return deferred.promise();		
	}
	
}


function refreshToken()	{
	/* get the authorization token[append to #auth_token] */
	var auth = oauth.refresh();

	auth.done(function(data) {
		console.warn(data);

		data = JSON.parse(data);
		var token = data.access_token;
		var refreshToken = data.refresh_token;

		console.log("Token: " + token);
		oauth.params.token = token;
		oauth.params.refreshToken = refreshToken;
	});

	auth.fail(function() {
		console.error("Error in client Id or client secret Id");
	});
}


function initToken()	{


		console.log("Init token");
		oauth.params.token = null;
		oauth.params.refreshToken = null;
}



function grant( code)	{
	
	var auth = oauth.grant( code);

	auth.done(function(data) {
		console.log(data);

		data = JSON.parse(data);
		var token = data.access_token;
		var refreshToken = data.refresh_token;

		console.log("Token: " + token);
		oauth.params.token = token;
		oauth.params.refreshToken = refreshToken;
	});

	auth.fail(function() {
		console.error("Bad dredentials");
	});
}

function supervise() {
	var url = oauth.params.resourceUrl+"/Admin.supervise";

	$JQry
			.ajax({
				type : "GET",
				url : url,
				headers : {
					'Content-Type' : undefined,
					"Authorization" : "Bearer " + oauth.getToken()
				},
				contentType : false,
				cache : false,
				timeout : 600000,
				success : function(jsonData) {
					if (jsonData.returnCode != 0)
						$JQry.notify("Error #"+jsonData.returnCode, "error");
					else {
						$JQry('#serverInfos').html(JSON.stringify(jsonData));
					}
				},
				error : function(xhr, status, e) {
					alert(e);
				}
			});

}




function drive(id) {
	var url = oauth.params.resourceUrl+"/Drive.content";

	if (typeof id !== 'undefined') {
		url = url + "?id=" + id;
	}

	$JQry
			.ajax({
				type : "GET",
				url : url,
				headers : {
					'Content-Type' : undefined,
					"Authorization" : "Bearer " + oauth.getToken()
				},
				contentType : false,
				cache : false,
				timeout : 600000,
				success : function(jsonData) {
					if (jsonData.returnCode != 0)
						$JQry.notify("Error #"+jsonData.returnCode, "error");
					else {
						var list = '';
						var detail = '';

						if (jsonData.type == 'file') {
							$JQry('#detail').show();
							$JQry('#contentId').val(jsonData.id);
							$JQry('#pubShare').attr("href",jsonData.shareUrl);
						} else {
							$JQry('#detail').hide();

							if (jsonData.type !== 'root') {
								// Breadcrumb
								for (var i = 0; i < jsonData.parents.length; i++) {
									list = list
											+ '<a  href="javascript:drive(\''
											+ jsonData.parents[i].id + '\')" >'
											+ jsonData.parents[i].title
											+ '</a> > ';
								}

								list += jsonData.title + "<br/>";

								// upper level
								var parentId = jsonData.parents[jsonData.parents.length - 1].id;
								list = list + '<a  href="javascript:drive(\''
										+ parentId + '\')" >..</a><br/>';
							}

							if (jsonData.childrens !== undefined) {
								for (var i = 0; i < jsonData.childrens.length; i++) {
									list = list + '<div class="row">';
									var child = jsonData.childrens[i];
									list = list
											+ '<div class="col-lg-5"> <a  href="javascript:drive(\''
											+ child.id + '\')" >' + child.title
											+ "</a></div>";
									list = list + '<div class="col-lg-2">';
									if (child.fileSize !== undefined) {
										list = list
												+ humanFileSize(child.fileSize,
														true);
									}
									list = list + '</div>';
									list = list
											+ '<div class="col-lg-4">'
											+ new Date(child.lastModified)
													.toLocaleString()
											+ "</div>";
									

									
									list = list + "</div>";
								}
							}
							$JQry('#folderId').val(jsonData.id);
							$JQry('#files').html(list);
						}
					}
				},
				error : function(xhr, status, e) {
					alert(e);
				}
			});

}

function addParameter (queryString, parameterName, parameterValue)	{
	if( parameterValue === undefined || parameterValue == "-")
		return queryString;
	
	if( queryString.length == 0)
		queryString += "?";
	else
		queryString += "&";	
	
	queryString = queryString + parameterName + "=" + parameterValue;
	
	return queryString;
}

function search(path, type, mimeType) {
	var url = oauth.params.resourceUrl+"/Drive.search";
	
	var queryString = "";
	queryString = addParameter (queryString, "path", path);
	queryString = addParameter (queryString, "type", type);
	queryString = addParameter (queryString, "mimeType", mimeType);

	url = url+queryString;

	$JQry
			.ajax({
				type : "GET",
				url : url,
				headers : {
					'Content-Type' : undefined,
					"Authorization" : "Bearer " + oauth.getToken()
				},
				contentType : false,
				cache : false,
				timeout : 600000,
				success : function(jsonData) {
					if (jsonData.returnCode != 0)
						$JQry.notify("Error #"+jsonData.returnCode, "error");
					else {
						var list = '';

						if (jsonData.items !== undefined) {
							for (var i = 0; i < jsonData.items.length; i++) {
								list = list + '<div class="row">';
								var child = jsonData.items[i];
								list = list
										+ '<div class="col-lg-5"> <a  href="javascript:drive(\''
										+ child.id + '\')" >' + child.title
										+ "</a></div>";
								list = list + '<div class="col-lg-2">';
								if (child.fileSize !== undefined) {
									list = list
											+ humanFileSize(child.fileSize,
													true);
								}
								list = list + '</div>';
								list = list
										+ '<div class="col-lg-4">'
										+ new Date(child.lastModified)
												.toLocaleString()
										+ "</div>";
								

								
								list = list + "</div>";
							}
						}

						$JQry('#treeFiles').html(list);
						
					}
				},
				error : function(xhr, status, e) {
					alert(e);
				}
			});

}

function download() {
	var url = oauth.params.resourceUrl+"/Drive.download";
	url = url + "?id=" + $JQry('#contentId').val();;


	$JQry
			.ajax({
				type : "GET",
				url : url,
				headers : {
					'Content-Type' : undefined,
					"Authorization" : "Bearer " + oauth.getToken()
				},
				contentType : false,
				cache : false,
				timeout : 600000,
				success : function() {
					alert("ok");
				},
				error : function(xhr, status, e) {
					alert(e);
				}
			});

}



$JQry(function() {

	$JQry("#btnUploadSubmit")
			.each(
					function(index, element) {

						var $element = $JQry(element);

						$element
								.click(function() {
									// Get form
									var form = $JQry('#fileUploadForm')[0];

									// Create an FormData object
									var data = new FormData(form);

									var params = {};
									params.parentId = $JQry('#folderId').val();
									params.parentPath = $JQry('#uploadParentFullName').val();
									params.ifFileExists = $JQry('#uploadActionIfExists').val();
									params.properties = buildProperties();
							
							

									data.append("uploadInfos", JSON
											.stringify(params));

									$JQry
											.ajax({
												type : "POST",
												url : oauth.params.resourceUrl+"/Drive.upload",
												headers : {
													'Content-Type' : undefined,
													"Authorization" : "Bearer " + oauth.getToken()
												},
												data : data,
												processData : false,
												contentType : false,
												cache : false,
												timeout : 600000,
												
												success : function(jsonData) {
													if (jsonData.returnCode != 0)
														$JQry.notify("Error #"+jsonData.returnCode, "error");
													else {
														$JQry.notify("Contenu créé", "success");
														drive($JQry('#folderId').val());														
													}

												},
												error : function(e) {
													$JQry.notify("HTTP Error #"+e.status, "error");
												}												
												
												
											});

								});
					});

	$JQry("#btnCreateFolder")
	.each(
			function(index, element) {

				var $element = $JQry(element);
				$element
						.click(function() {
							var params = {};
							params.parentId = $JQry('#folderId').val();							
							params.folderName = $JQry('#folderName').val();
							$JQry
									.ajax({
										type : "POST",
										url : oauth.params.resourceUrl+"/Drive.createFolder",
										headers : {
											"Authorization" : "Bearer " + oauth.getToken()
										},
										dataType : 'json',
										contentType : 'application/json',
										data : JSON.stringify(params),
										
										success : function(jsonData) {
											if (jsonData.returnCode != 0)
												$JQry.notify("Error #"+jsonData.returnCode, "error");
											else	{
												$JQry.notify("Dossier créé", "success");
												drive($JQry('#folderId').val());													
											}

										},
										error : function(e) {
											$JQry.notify("HTTP Error #"+e.status, "error");
										}
										
									});

						});
			});

	$JQry("#btnGetSharedUrl")
	.each(
			function(index, element) {

				var $element = $JQry(element);
				$element
						.click(function() {
							var params = {};
							params.contentId = $JQry('#contentId').val();
							params.format = $JQry('#sharedFormat').val();
							params.publish =  $JQry('#sharedPublish').val();
							$JQry
									.ajax({
										type : "POST",
										url : oauth.params.resourceUrl+"/Drive.getShareUrl",
										headers : {
											"Authorization" : "Bearer " + oauth.getToken()
										},
										dataType : 'json',
										contentType : 'application/json',
										data : JSON.stringify(params),
										
										success : function(jsonData) {
											if (jsonData.returnCode != 0)
												$JQry.notify("Error #"+jsonData.returnCode, "error");
											else	{
												$JQry.notify("Get Share Url", "success");
												$JQry('#shareUrl').val(jsonData.shareUrl);
											}

										},
										error : function(e) {
											$JQry.notify("HTTP Error #"+e.status, "error");
										}
										
									});

						});
			});
	
	
	
	
	$JQry("#btnPubSubmit")
			.each(
					function(index, element) {

						var $element = $JQry(element);
						$element
								.click(function() {
									var params = {};
									params.shareUrl = $JQry('#shareUrl').val();
									params.schoolYear = "2019"
									params.pubContext = "CPLC_CahierDeTexteContenu";	
									var pubGroups = [];
									pubGroups[0] = "3A";
									pubGroups[1] = "4A";
									params.pubGroups = pubGroups;									
																		
									params.properties = buildProperties();
									
									$JQry
											.ajax({
												type : "POST",
												url : oauth.params.resourceUrl+"/Drive.publish",
												headers : {
													"Authorization" : "Bearer " + oauth.getToken()
												},
												dataType : 'json',
												contentType : 'application/json',
												data : JSON.stringify(params),
												
												success : function(jsonData) {
													if (jsonData.returnCode != 0)
														$JQry.notify("Error #"+jsonData.returnCode, "error");
													else	{
														$JQry.notify("Contenu publié", "success");
														$JQry('#pubShare').attr("href",$JQry('#shareUrl').val());
														$JQry('#unpubId').val(jsonData.pubId);
													}

												},
												error : function(e) {
													$JQry.notify("HTTP Error #"+e.status, "error");
												}
												
											});

								});
					});

	$JQry("#btnUnpubSubmit")
	.each(
			function(index, element) {

				var $element = $JQry(element);
				$element
						.click(function() {
							var params = {};
							params.pubId = $JQry('#unpubId').val();


							$JQry
									.ajax({
										type : "POST",
										url : oauth.params.resourceUrl+"/Drive.unpublish",
										headers : {
											"Authorization" : "Bearer " + oauth.getToken()
										},
										dataType : 'json',
										contentType : 'application/json',
										data : JSON.stringify(params),
										
										success : function(jsonData) {
											if (jsonData.returnCode != 0)
												$JQry.notify("Error #"+jsonData.returnCode, "error");
											else
												$JQry.notify("Contenu dépublié", "success");

										},
										error : function(e) {
											$JQry.notify("HTTP Error #"+e.status, "error");
										}
										
									});

						});
			});



	$JQry("#OAuth2authenticationCredentials").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {


			var username = $JQry('#authUserId').val();
			var password = $JQry('#authUserPassword').val();
			
			/* get the authorization token[append to #auth_token] */
			var auth = oauth.authenticate(username, password);

			auth.done(function(data) {
				console.warn(data);

				data = JSON.parse(data);
				var token = data.access_token;
				var refreshToken = data.refresh_token;

				console.log("Token: " + token);
				oauth.params.token = token;
				oauth.params.refreshToken = refreshToken;
			});

			auth.fail(function() {
				console.error("Error in client Id or client secret Id");
			});

		});
	});
	

	$JQry("#OAuth2Authorize").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {

			oauth.params.state = Math.floor(Math.random() * 10000); 
			var newLocation = oauth.params.authorizeUrl + "?client_id="+oauth.params.clientId+"&redirect_uri="+oauth.params.clientUrl+"&response_type=code&scope="+oauth.params.scope+"&state="+oauth.params.state;
			window.location.href = newLocation;
		});
	});
	
	
	$JQry("#OAuth2AuthorizeNoState").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {


			var newLocation = oauth.params.authorizeUrl + "?client_id="+oauth.params.clientId+"&redirect_uri="+oauth.params.clientUrl+"&response_type=code&scope="+oauth.params.scope;
			window.location.href = newLocation;
		});
	});
	
	
	
	$JQry("#OAuth2refreshToken").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {

			refreshToken();


		});
	});
	
	
	$JQry("#OAuth2initToken").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			initToken();
		});
	});

	$JQry("#btnRefreshDrive").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {

			drive();
		});
	});
	
	$JQry("#btnSupervise").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {

			supervise();
		});
	});
	
	

	$JQry("#btnGetTree").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			var treePath = $JQry('#getTreeFullName').val();
			var treeType = $JQry('#getType').val();
			var treeMimeType = $JQry('#getMimeType').val();
			search(treePath, treeType, treeMimeType);
		});
	});
	
	
	$JQry("#btnDownload").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			download();
		});
	});
	
	
	$JQry("#btnUserProfile").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
				var url = oauth.params.resourceUrl+"/User.getProfile";



				$JQry
						.ajax({
							type : "GET",
							url : url,
							headers : {
								'Content-Type' : undefined,
								"Authorization" : "Bearer " + oauth.getToken()
							},
							contentType : false,
							cache : false,
							timeout : 600000,
							success : function(jsonData) {
								if (jsonData.returnCode != 0)
									$JQry.notify("Error #"+jsonData.returnCode, "error");
								else {
										$JQry.notify("profile id:"+jsonData.id, "success");									
									}
							},
							error : function(xhr, status, e) {
								alert(e);
							}
						});
	
		});
		
	});
	
	
	
	$JQry("#btnCreateUser").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			console.log("demande création utilisateur ");			


			var params = {};
			params.firstName = $JQry('#userFirstName').val();
			params.lastName = $JQry('#userLastName').val();
			params.mail = $JQry('#userMail').val();


			$JQry
					.ajax({
						type : "POST",
						url : oauth.params.resourceUrl+"/Admin.createUser",
						headers : {
							"Authorization" : "Bearer " + oauth.getToken()
						},
						dataType : 'json',
						contentType : 'application/json',
						data : JSON.stringify(params),
						success : function(jsonData) {
							if (jsonData.returnCode != 0)
								$JQry.notify("Error #"+jsonData.returnCode, "error");
							else
								$JQry.notify("Utilisateur créé", "success");

						},
						error : function(e) {
							$JQry.notify("HTTP Error #"+e.status, "error");
						}
					});

			});
	});
	
	
	$JQry("#btnError").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			console.log("génération d'une erreur ");			

			testError( "error");
			});
	});
	
	
	
	$JQry("#btnException").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			console.log("génération d'une erreur ");			

			testError( "exception");
			});
	});	
	
	$JQry("#btnNotFound").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			console.log("génération d'une erreur ");			

			drive( "????????");
			});
	});		

	
	
	$JQry("#btnSignUp").each(function(index, element) {

		var $element = $JQry(element);
		$element.click(function() {
			console.log("signup utilisateur ");			

			$JQry
					.ajax({
						type : "GET",
						url : oauth.params.resourceUrl+"/User.signup",
						headers : {
							"Authorization" : "Bearer " + createSignUpToken()
						},
						dataType : 'json',
						contentType : 'application/json',
						success : function(jsonData) {
							if (jsonData.returnCode != 0)
								$JQry.notify("Error #"+jsonData.returnCode+ " "+ jsonData.errorMessage, "error");
							else{
								$JQry.notify("Utilisateur créé", "success");
								window.open(jsonData.url,"signup");
							}

						},
						error : function(e) {
							$JQry.notify("HTTP Error #"+e.status, "error");
						}
					});

			});
	});	
	
	
	
	$JQry("#btnRSASignUp").each(function(index, element) {

		var $element = $JQry(element);
		var token = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJSUzI1NiJ9.eyJmaXJzdE5hbWUiOiJQUk9GRVNTRVVSIiwNCiJsYXN0TmFtZSI6Ik1heGltZSIsDQoibWFpbCI6Im1heGltZS5wcm9mZXNzZXVyQGZvdXJuaXNzZXVyLmZyIiwNCiJpc3MiOiJwcm9ub3RlIiwNCiJpYXQiOiIxNTU2MjIxODcyIn0.gj0OzffC7ARHZTJ02N10lKXHKDNafqe7RkfG1Y0PoDhv3Dq9V_a0mbJDNg6BUWh3cNUGC7rIjEBfOXEs3vRd9iq9AR5mucf9CrLX6CeztAftBewbcWN_9fC4dnoDOAkYLBaC8Hc7-asRPuGN2mY_V_wImTgnrRhj7kUnvAj1TvWH7x3UNG63IlTHrkRIBzQdK63cPWFl4cpvHIsbrSa5d4dKwhqNvs5VaBC1bwnKu4Au5LK5T5neLnEW3dYS5qxfENp2YeFzUBINPQZu_oFBK2O3gJzXQ3CJqyqd8OdDKHkxxmYmeaZ_U_1qd8meuS4hoIbbk_zWGoXjrsTk-9AXsQ"
		
		
		$element.click(function() {
			console.log("signup RSA ");			

			$JQry
					.ajax({
						type : "GET",
						url : oauth.params.resourceUrl+"/User.signup",
						headers : {
							"Authorization" : "Bearer " + token
						},
						dataType : 'json',
						contentType : 'application/json',
						success : function(jsonData) {
							if (jsonData.returnCode != 0)
								$JQry.notify("Error #"+jsonData.returnCode+ " "+ jsonData.errorMessage, "error");
							else{
								$JQry.notify("Utilisateur créé", "success");
								window.open(jsonData.url,"signup","menubar=no, status=no, scrollbars=no, menubar=no, width=1000, height=600");
							}

						},
						error : function(e) {
							$JQry.notify("HTTP Error #"+e.status, "error");
						}
					});

			});
	});	
});


function testError( type) {
	$JQry
	.ajax({
		type : "GET",
		url : oauth.params.resourceUrl+"/Drive.error?type="+type,
		headers : {
			"Authorization" : "Bearer " + oauth.getToken()
		},
		dataType : 'json',
		contentType : 'application/json',
		success : function(jsonData) {
			if (jsonData.returnCode != 0)
				$JQry.notify("Error #"+jsonData.returnCode, "error");
		},
		error : function(e) {
			$JQry.notify("HTTP Error #"+e.status, "error");
		}
	});
	
}

function createSignUpToken( )	{
	// Defining our token parts
	var header = {
		"alg" : "HS256",
		"typ" : "JWT"
	};


	var data = {	};
	data.firstName = $JQry('#userFirstName').val();
	data.lastName = $JQry('#userLastName').val();
	data.mail = $JQry('#userMail').val();
	data.iss = "pronote";

	var secret = "??PRONOTESECRET??";

	var stringifiedHeader = CryptoJS.enc.Utf8.parse(JSON.stringify(header));
	var encodedHeader = base64url(stringifiedHeader);

	var stringifiedData = CryptoJS.enc.Utf8.parse(JSON.stringify(data));
	var encodedData = base64url(stringifiedData);

	var signature = encodedHeader + "." + encodedData;
	signature = CryptoJS.HmacSHA256(signature, secret);
	signature = base64url(signature);

	return encodedHeader+"."+ encodedData+"."+signature;
}

function base64url(source) {
	// Encode in classical base64
	encodedSource = CryptoJS.enc.Base64.stringify(source);

	// Remove padding equal characters
	encodedSource = encodedSource.replace(/=+$/, '');

	// Replace characters according to base64url specifications
	encodedSource = encodedSource.replace(/\+/g, '-');
	encodedSource = encodedSource.replace(/\//g, '_');

	return encodedSource;
}

function buildProperties() {

	var properties = {};
	properties.documentType = "4";
	
	var levels = [];
	var level = {};
	level.name = "4EME";
	var codes = [];
	codes[0] = "22122231016";
	level.codes = codes;
	levels[0] = level;
	
	var level = {};
	level.name = "3EME";
	var codes = [];
	codes[0] = "21160010026";
	codes[1] = "24340010002";
	level.codes = codes;
	levels[1] = level;
	
	properties.levels= levels;
	
	
	var subject = {};
	subject.name = "ESPAGNOL LV2";
	var codes = [];
	codes[0] = "030602";
	subject.codes = codes;
	
	properties.subject= subject;
	
	var keywords = [];
	keywords[0] = "hh";
	keywords[1] = "ii";
	
	properties.keywords= keywords;
	
	return properties
}


function init() {
    var code = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === 'code') code = decodeURIComponent(tmp[1]);
    }
    
    var host = "https://"+window.location.hostname;
    //var host = https://cloud-ens-ws.osivia.org
    
    oauth.params.accessUrl = host+'/index-cloud-portal-ens-ws/oauth/token';
    oauth.params.authorizeUrl= host+'/index-cloud-portal-ens-ws/oauth/authorize';  
    oauth.params.resourceUrl = host+'/index-cloud-portal-ens-ws/rest';
	oauth.params.clientId =  'PRONOTE-1234';
	oauth.params.clientSecretId = 'secret1234';
	oauth.params.scope = 'drive';
	oauth.params.clientUrl = host+'/index-cloud-portal-ens-ws/html/test.html';	
    
   if( code != null)	{
	   grant(code);
   }
}

