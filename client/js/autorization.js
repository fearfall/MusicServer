$(document).ready(function(){
	//for authorization
	 $(".signin").click(function(e) {
		e.preventDefault();
		$("fieldset#signin_menu").toggle();
		$(".signin").toggleClass("menu-open");
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


