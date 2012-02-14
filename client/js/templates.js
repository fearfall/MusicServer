//$(document).ready(function(){ 
    var ip = "192.168.211.63";
    var searchUrl    = "http://"+ ip +":6006/search/?format=json&jsoncallback=?";
    var getArtistUrl = "http://"+ ip +":6006/get/artist?format=json&jsoncallback=?";
	var getAlbumUrl  = "http://"+ ip +":6006/get/album?format=json&jsoncallback=?";
    
    function adjustPlaylist() {
        $(".playlist").scrollable({
		items:'.playlist',
		vertical:true,
		mousewheel: true
	});
    }
    
    function viewSearchResults(pattern) {
        $('.results').replaceWith("\
        <div class='results'>\
             <div id='tabs'>\
                <ul> \
                    <li><a href='#artists'>Artists</a></li> \
                    <li><a href='#albums'>Albums</a></li>\
                    <li><a href='#tracks'>Tracks</a></li>\
                </ul>\
                <div id='artists' class='content_item'/>\
                <div id='albums' class='content_item'/>\
                <div id='tracks' class='content_item'/> \
            </div>\
        </div>");
		$.getJSON(searchUrl, {'pattern' : pattern}, function(json) {
                        $( "#tabs" ).tabs();
						applyTemplate("#artist_expanded_template", json.artists, "#artists");
                        applyTemplate("#album_expanded_template", json.albums, "#albums");
						addHiding(".artist .title .expand_button");
                        addHiding(".album .title .expand_button");
						applyTemplate("#tracks_template", json, "#tracks");
                        addButtons();
                        playButtons();
					});
	}
    
	function applyTemplate(templateName, data, element) {
	  $(templateName).tmpl(data).appendTo(element);
    }
	function addButtons(){
        $(".add_button").button();
        $(".add_button").click(function() {                           
                            addToPlaylist("#clips", $(this));
                        });
    }  
    
    function playButtons(){
        $(".play_button").button();
        $(".play_button").click(function() {                           
                            addToPlaylist("#clips", $(this));
                        });
    } 
    
	function addHiding(element) {
		$(element).click(function() {
				$(this).parent().parent().next().toggle();
				return false;
			}).parent().parent().next().hide();
	}
    
    function addToPlaylist(playlist_element, button) {
        var id = button.parent().parent().attr('id')
        var getTrackUrl  = "http://"+ ip +":6006/get/track?format=json&jsoncallback=?";
		$.getJSON(getTrackUrl, {'id' : id}, function(json) {
						        //applyTemplate("playlist_track_template", json, playlist_element);	
                                $("#playlist_item_template").tmpl(json).appendTo(playlist_element);
					});
    }
    function getUrlVars() {
        var vars = [], hash;
        var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
        for(var i = 0; i < hashes.length; i++)
        {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    }
    function initPlayer() {
		$f("footer_player").playlist("#clips:first", {loop:true});
    }
