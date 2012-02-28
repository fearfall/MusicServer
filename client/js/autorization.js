$(document).ready(function(){
	//for authorization
    
    var credentials = "";
	var userPlaylists = "";
	
	$.ajaxSetup({
		type: 'POST',
		dataType: 'jsonp',
		jsonp: 'callback',
		async: true,
		crossDomain: true,
		global: true,
		timeout: 5000
	});
	
	function authServer_makeAjaxRequest(url, buffer, errorMsg) {
		return $.ajax({
			url: url,
			data: credentials,
			success: function (data) {
				buffer = data;
				setLoggedIn();
				alert(buffer);
			},
			error: function() { 
				alert(errorMsg); 
				setLoggedOut();
			}
		});
		
	}
	
	
	function getCredentials() {
		var username = $("#username").val();
		var pwd = $("#password").val();
		if (username.length == 0 || pwd.length == 0) {
			return false;
		} else {
			credentials = "credentials=Basic "+ $.base64Encode(username+":"+pwd);
		}
		return true;
	}
	
	 $("#topnav a.signin").click(function(e) {
		e.preventDefault();
		if ($("#topnav a.signin").hasClass("loggedin")) {
			setLoggedOut();
		} else {
			$("fieldset#signin_menu").toggle();
			$(".signin").toggleClass("menu-open");
		}
	 });
     
     function clearUserData() {
		credentials = "";
		userPlaylists = "";
	 }
     
     function setLoggedOut() {
		clearUserData();
		$("#topnav a.signin").removeClass("loggedin");
		document.getElementById("sign_title").childNodes[0].nodeValue = "Вход";
		alert("Logged out");
	 }
     
     function setLoggedIn() {
		$("fieldset#signin_menu").toggle();
		$(".signin").removeClass("menu-open");
		$("fieldset#signin_menu").hide();
		$("#topnav a.signin").addClass("loggedin");
		document.getElementById("sign_title").childNodes[0].nodeValue = "Выход";
	 }
     function suggestLogin() {}
      
     $("#signin_submit").click(function(e) {
		e.preventDefault();
		if (getCredentials()) {
			authServer_makeAjaxRequest("https://localhost:8443/playlist/?action=getall", userPlaylists, "Login Error!");
		} else {
			alert("Please enter your login and password");
			return false;
		}
		return true;
	});
 
     $("fieldset#signin_menu").mouseup(function() {
			return false;
	 });
     
     $(document).mouseup(function(e) {
		if($(e.target).parent("a.signin").length==0) {
			$(".signin").removeClass("menu-open");
			$("fieldset#signin_menu").hide();
		}
     });            
            
	//end for authorization
});


