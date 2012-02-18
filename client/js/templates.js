	var ip = "192.168.211.73";
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
    
    var currentTab = 1;
    function initTabs() {
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
        var tabs = $( "#tabs" ).tabs();
        tabs.bind( "tabsselect", function(event, ui) {
			var selected = $(ui.index);
			if(selected.length === 0)
				selected = 1;
			else {
				selected = selected[0] + 1;
				}
			currentTab = selected;
			if (!checkLoadedTabs(selected)) {
				var pattern = $('#search_field').val();
				makeTypifiedSearch(selected, pattern);
			}
	    });
	}
    
    function clearTabs() {
		$("#artists").empty();
		$("#albums").empty();
		$("#tracks").empty();
	}
	
	function makeTypifiedSearch(type, pattern) {
		switch (type) {
			case 1:
				$.getJSON(searchUrl, {'type': 1, 'pattern' : pattern}, function(json) {
					if(json[0] !== "Error") {
						applyTemplate("#artist_expanded_template", json.artists, "#artists");
						addHiding(".artist .title .expand_button");
						addButtons();
						playButtons();
						cacheArtists(json.artists.models);
					} else {
						$("#artists").append(json[0]);
					}
				});
				break
			case 2:
				$.getJSON(searchUrl, {'type': 2, 'pattern' : pattern}, function(json) {
					if(json[0] !== "Error") {
						applyTemplate("#album_expanded_template", json.albums, "#albums");
						addHiding(".album .title .expand_button");
						addButtons();
						playButtons();
						cacheAlbums(json.albums.models);
					}
					else {
						$("#albums").append(json[0]);
					}
				});
				break
			case 3:
				$.getJSON(searchUrl, {'type': 3, 'pattern' : pattern}, function(json) {
					if(json) {
						applyTemplate("#tracks_template", json, "#tracks");
						addButtons();
						playButtons();
						cacheTracks(json.tracks.models);
					}
				});
				break
			default:
				
		}
	}
    
    function viewSearchResults() {
		var search_field = $('#search_field');
		var pattern = search_field.val();
		clearCache();
        clearTabs();
        makeTypifiedSearch(currentTab, pattern);
	}
    
	function applyTemplate(templateName, data, element) {
	  if(data.length === 0)
		$(element).append("<div>No results</div>");
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
				var mbid = $(this).parent().parent().parent().attr("id");
				if (!checkLoadedObject(mbid)) {
					switch (getObject(mbid).type) {
						case "artist": 
							
							break
						case "album": break
						default:
					}
				}
				return false;
			}).parent().parent().next().hide();
	}
	
	function fillArtistAlbums(mbid) {
		
	}
    
    function addToPlaylist(playlist_element, button) {
        var id = button.parent().parent().attr('id')
        var getTrackUrl  = "http://"+ ip +":6006/get/track?format=json&jsoncallback=?";
        playlist_element.append();
		$.getJSON(getTrackUrl, {'id' : id}, function(json) {
						        	
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
